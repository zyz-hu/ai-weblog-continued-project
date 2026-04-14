package com.zhouyuanzhi.ai.robot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.zhouyuanzhi.ai.robot.domain.dos.AiCustomerServiceMdStorageDO;
import com.zhouyuanzhi.ai.robot.domain.mapper.AiCustomerServiceMdStorageMapper;
import com.zhouyuanzhi.ai.robot.enums.AiCustomerServiceMdStatusEnum;
import com.zhouyuanzhi.ai.robot.enums.ResponseCodeEnum;
import com.zhouyuanzhi.ai.robot.event.AiCustomerServiceMdUploadedEvent;
import com.zhouyuanzhi.ai.robot.exception.BizException;
import com.zhouyuanzhi.ai.robot.model.vo.chat.AIResponse;
import com.zhouyuanzhi.ai.robot.model.vo.knowledge.KnowledgeChatReqVO;
import com.zhouyuanzhi.ai.robot.model.vo.knowledge.KnowledgeFilePageReqVO;
import com.zhouyuanzhi.ai.robot.model.vo.knowledge.KnowledgeFilePageRspVO;
import com.zhouyuanzhi.ai.robot.model.vo.knowledge.KnowledgeFileUpdateReqVO;
import com.zhouyuanzhi.ai.robot.repository.VectorStoreRepository;
import com.zhouyuanzhi.ai.robot.service.CustomerService;
import com.zhouyuanzhi.ai.robot.strategy.model.AIModelStrategy;
import com.zhouyuanzhi.ai.robot.factory.AIModelFactory;
import com.zhouyuanzhi.ai.robot.utils.PageResponse;
import com.zhouyuanzhi.ai.robot.utils.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 私有知识库
 */
@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Value("${customer-service.md-storage-path}")
    private String mdStoragePath;

    @Resource
    private AiCustomerServiceMdStorageMapper aiCustomerServiceMdStorageMapper;
    @Resource
    private ApplicationEventPublisher eventPublisher;
    @Resource
    private VectorStore vectorStore;
    @Resource
    private VectorStoreRepository vectorStoreRepository;
    @Resource
    private AIModelFactory aiModelFactory;

    @Override
    public Response<?> uploadMarkdownFile(MultipartFile file, String remark) {
        if (file == null || file.isEmpty()) {
            throw new BizException(ResponseCodeEnum.UPLOAD_FILE_CANT_EMPTY);
        }

        String originalFilename = StringUtils.trimToEmpty(file.getOriginalFilename());
        if (StringUtils.isBlank(originalFilename) || !isMarkdownFile(originalFilename)) {
            throw new BizException(ResponseCodeEnum.ONLY_SUPPORT_MARKDOWN);
        }

        try {
            String newFilename = UUID.randomUUID().toString() + "-" + originalFilename;
            Path storageDirectory = resolveStorageDirectory();
            Path targetPath = storageDirectory.resolve(newFilename);
            file.transferTo(targetPath.toFile());

            AiCustomerServiceMdStorageDO record = AiCustomerServiceMdStorageDO.builder()
                    .originalFileName(originalFilename)
                    .newFileName(newFilename)
                    .filePath(targetPath.toString())
                    .fileSize(file.getSize())
                    .remark(remark)
                    .status(AiCustomerServiceMdStatusEnum.PENDING.getCode())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            aiCustomerServiceMdStorageMapper.insert(record);
            Long id = record.getId();

            Map<String, Object> metadatas = Maps.newHashMap();
            metadatas.put("mdStorageId", id);
            metadatas.put("originalFileName", originalFilename);
            metadatas.put("filePath", targetPath.toString());

            //发布监听事件
            eventPublisher.publishEvent(AiCustomerServiceMdUploadedEvent.builder()
                    .id(id)
                    .filePath(targetPath.toString())
                    .metadatas(metadatas)
                    .build());

            return Response.success();
        } catch (IOException e) {
            log.error("upload markdown failed: {}", originalFilename, e);
            throw new BizException(ResponseCodeEnum.UPLOAD_FILE_FAILED);
        }
    }

    @Override
    public PageResponse<KnowledgeFilePageRspVO> pageKnowledgeFiles(KnowledgeFilePageReqVO reqVO) {
        Long current = reqVO.getCurrent();
        Long size = reqVO.getSize();

        LambdaQueryWrapper<AiCustomerServiceMdStorageDO> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(reqVO.getKeyword())) {
            wrapper.like(AiCustomerServiceMdStorageDO::getOriginalFileName, reqVO.getKeyword());
        }
        if (reqVO.getStatus() != null) {
            wrapper.eq(AiCustomerServiceMdStorageDO::getStatus, reqVO.getStatus());
        }
        wrapper.orderByDesc(AiCustomerServiceMdStorageDO::getUpdateTime);

        Page<AiCustomerServiceMdStorageDO> page = aiCustomerServiceMdStorageMapper.selectPage(new Page<>(current, size), wrapper);
        List<KnowledgeFilePageRspVO> vos = page.getRecords().stream()
                .map(record -> KnowledgeFilePageRspVO.builder()
                        .id(record.getId())
                        .originalFileName(record.getOriginalFileName())
                        .newFileName(record.getNewFileName())
                        .remark(record.getRemark())
                        .status(record.getStatus())
                        .fileSize(record.getFileSize())
                        .createTime(record.getCreateTime())
                        .updateTime(record.getUpdateTime())
                        .build())
                .collect(Collectors.toList());

        return PageResponse.success(page, vos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> updateKnowledgeFile(KnowledgeFileUpdateReqVO reqVO, MultipartFile file) {
        AiCustomerServiceMdStorageDO record = aiCustomerServiceMdStorageMapper.selectById(reqVO.getId());
        if (record == null) {
            throw new BizException(ResponseCodeEnum.MD_STORAGE_NOT_FOUND);
        }

        String remark = StringUtils.trimToNull(reqVO.getRemark());
        boolean hasNewFile = file != null && !file.isEmpty();

        if (hasNewFile && !isMarkdownFile(file.getOriginalFilename())) {
            throw new BizException(ResponseCodeEnum.ONLY_SUPPORT_MARKDOWN);
        }

        if (hasNewFile) {
            try {
                deleteLocalFileIfExists(record.getFilePath());

                String newFilename = UUID.randomUUID().toString() + "-" + StringUtils.trimToEmpty(file.getOriginalFilename());
                Path storageDirectory = resolveStorageDirectory();
                Path targetPath = storageDirectory.resolve(newFilename);
                file.transferTo(targetPath.toFile());

                record.setOriginalFileName(StringUtils.trimToEmpty(file.getOriginalFilename()));
                record.setNewFileName(newFilename);
                record.setFilePath(targetPath.toString());
                record.setFileSize(file.getSize());
                record.setStatus(AiCustomerServiceMdStatusEnum.PENDING.getCode());
                record.setUpdateTime(LocalDateTime.now());
                record.setRemark(remark);
                aiCustomerServiceMdStorageMapper.updateById(record);

                vectorStoreRepository.deleteByMdStorageId(record.getId());

                Map<String, Object> metadatas = Maps.newHashMap();
                metadatas.put("mdStorageId", record.getId());
                metadatas.put("originalFileName", record.getOriginalFileName());
                metadatas.put("filePath", record.getFilePath());

                eventPublisher.publishEvent(AiCustomerServiceMdUploadedEvent.builder()
                        .id(record.getId())
                        .filePath(record.getFilePath())
                        .metadatas(metadatas)
                        .build());
            } catch (IOException ex) {
                log.error("update markdown failed, id={}", record.getId(), ex);
                throw new BizException(ResponseCodeEnum.UPLOAD_FILE_FAILED);
            }
        } else {
            record.setRemark(remark);
            record.setUpdateTime(LocalDateTime.now());
            aiCustomerServiceMdStorageMapper.updateById(record);
        }

        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> deleteKnowledgeFile(Long id) throws IOException {
        AiCustomerServiceMdStorageDO record = aiCustomerServiceMdStorageMapper.selectById(id);
        if (record == null) {
            throw new BizException(ResponseCodeEnum.MD_STORAGE_NOT_FOUND);
        }

        vectorStoreRepository.deleteByMdStorageId(id);
        aiCustomerServiceMdStorageMapper.deleteById(id);
        deleteLocalFileIfExists(record.getFilePath());

        return Response.success();
    }

    @Override
    public Flux<AIResponse> knowledgeChat(KnowledgeChatReqVO reqVO) {
        String question = reqVO.getMessage();
        Integer topK = reqVO.getTopK() == null ? 4 : reqVO.getTopK();

        List<Long> limitedIds = reqVO.getMdStorageIds();
        validateMdStatusReady(limitedIds);

        List<Document> documents = new ArrayList<>();
        try {
            SearchRequest searchBuilder = SearchRequest.builder()
                    .query(question)
                    .topK(topK)
                    .build();
            documents = vectorStore.similaritySearch(searchBuilder);
        } catch (Exception e) {
            log.error("vector search failed", e);
            throw new BizException(ResponseCodeEnum.RAG_QUERY_FAILED);
        }

        if (limitedIds != null && !limitedIds.isEmpty()) {
            documents = documents.stream()
                    .filter(doc -> {
                        Object mdId = doc.getMetadata().get("mdStorageId");
                        return mdId != null && limitedIds.contains(Long.parseLong(mdId.toString()));
                    })
                    .toList();
        }

        String context = buildContext(documents);
        String prompt = buildRagPrompt(question, context);

        AIModelStrategy strategy = aiModelFactory.getStrategy(reqVO.getModelName());
        AtomicBoolean isStreamFinished = new AtomicBoolean(false);

        Flux<AIResponse> aiStream = strategy.streamResponse(reqVO.getModelName(), prompt, reqVO.getTemperature(), java.util.List.of())
                .doOnTerminate(() -> isStreamFinished.set(true))
                .onErrorResume(e -> {
                    log.error("knowledge chat error", e);
                    return Flux.just(AIResponse.builder().v("服务异常: " + e.getMessage()).type("content").build());
                });

        Flux<AIResponse> heartbeatStream = Flux.interval(Duration.ofSeconds(1))
                .takeWhile(i -> !isStreamFinished.get())
                .map(i -> AIResponse.builder().type("ping").v("").build());

        return Flux.merge(aiStream, heartbeatStream);
    }

    private boolean isMarkdownFile(String filename) {
        if (StringUtils.isBlank(filename)) {
            return false;
        }
        String extension = FilenameUtils.getExtension(filename);
        return StringUtils.equalsIgnoreCase(extension, "md");
    }

    private Path resolveStorageDirectory() throws IOException {
        Path storageDirectory = Paths.get(StringUtils.defaultIfBlank(mdStoragePath, "./data/md"))
                .toAbsolutePath()
                .normalize();
        Files.createDirectories(storageDirectory);
        return storageDirectory;
    }

    private void deleteLocalFileIfExists(String path) throws IOException {
        if (StringUtils.isBlank(path)) {
            return;
        }
        Files.deleteIfExists(Paths.get(path));
    }

    private void validateMdStatusReady(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<AiCustomerServiceMdStorageDO> records = aiCustomerServiceMdStorageMapper.selectBatchIds(ids);
        if (records.size() != ids.size()) {
            throw new BizException(ResponseCodeEnum.MD_STORAGE_NOT_FOUND);
        }
        boolean allReady = records.stream().allMatch(r -> AiCustomerServiceMdStatusEnum.COMPLETED.getCode().equals(r.getStatus()));
        if (!allReady) {
            throw new BizException(ResponseCodeEnum.MD_STATUS_NOT_READY);
        }
    }

    private String buildContext(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return "无匹配知识片段。";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < documents.size(); i++) {
            Document document = documents.get(i);
            builder.append("片段").append(i + 1).append(":\n")
                    .append(document.getText()).append("\n");
            Map<String, Object> metadata = document.getMetadata();
            if (metadata != null && metadata.containsKey("originalFileName")) {
                builder.append("来源文件: ").append(metadata.get("originalFileName")).append("\n");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private String buildRagPrompt(String question, String context) {
        return """
                你是企业私有知识库的智能客服，请严格根据给定的知识内容回答用户问题。
                - 如果知识库没有相关内容，请直接回复不知道，不要编造。
                - 回复语言跟随用户输入。
                
                知识内容:
                %s
                
                用户问题:
                %s
                """.formatted(context, question);
    }
}
