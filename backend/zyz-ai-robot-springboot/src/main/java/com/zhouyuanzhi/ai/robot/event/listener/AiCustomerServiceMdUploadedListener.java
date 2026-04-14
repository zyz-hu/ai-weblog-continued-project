package com.zhouyuanzhi.ai.robot.event.listener;

import com.zhouyuanzhi.ai.robot.domain.dos.AiCustomerServiceMdStorageDO;
import com.zhouyuanzhi.ai.robot.domain.mapper.AiCustomerServiceMdStorageMapper;
import com.zhouyuanzhi.ai.robot.enums.AiCustomerServiceMdStatusEnum;
import com.zhouyuanzhi.ai.robot.event.AiCustomerServiceMdUploadedEvent;
import com.zhouyuanzhi.ai.robot.reader.MarkdownReader;
import com.zhouyuanzhi.ai.robot.repository.VectorStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Markdown 上传事件监听
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AiCustomerServiceMdUploadedListener {

    private static final int EMBEDDING_BATCH_LIMIT = 10;

    private final MarkdownReader markdownReader;
    private final VectorStore vectorStore;
    private final VectorStoreRepository vectorStoreRepository;
    private final AiCustomerServiceMdStorageMapper aiCustomerServiceMdStorageMapper;
    private final TransactionTemplate transactionTemplate;

    /**
     * Markdown 文件向量化
     */
    @EventListener
    @Async("eventTaskExecutor")
    public void vectorizing(AiCustomerServiceMdUploadedEvent event) {
        log.info("handle AiCustomerServiceMdUploadedEvent: {}", event);

        Long id = event.getId();
        String filePath = event.getFilePath();
        Map<String, Object> metadatas = event.getMetadatas();

        aiCustomerServiceMdStorageMapper.updateById(AiCustomerServiceMdStorageDO.builder()
                .id(id)
                .status(AiCustomerServiceMdStatusEnum.VECTORIZING.getCode())
                .updateTime(LocalDateTime.now())
                .build());

        boolean isSuccess = Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            try {
                var resource = new FileSystemResource(filePath);
                if (!resource.exists() || !resource.isReadable()) {
                    throw new IllegalStateException("markdown file not readable: " + filePath);
                }
                List<Document> documents = markdownReader.loadMarkdown(resource, metadatas).stream()
                        .filter(this::hasVectorizableText)
                        .toList();
                if (documents.isEmpty()) {
                    throw new IllegalStateException("markdown contains no vectorizable content, id=" + id);
                }

                vectorStoreRepository.deleteByMdStorageId(id);
                addDocumentsInBatches(id, documents);
                log.info("markdown vectorized successfully, id={}, chunks={}", id, documents.size());

                aiCustomerServiceMdStorageMapper.updateById(AiCustomerServiceMdStorageDO.builder()
                        .id(id)
                        .status(AiCustomerServiceMdStatusEnum.COMPLETED.getCode())
                        .updateTime(LocalDateTime.now())
                        .build());
                return true;
            } catch (Exception ex) {
                log.error("markdown vectorizing failed, event={}", event, ex);
                status.setRollbackOnly();
                return false;
            }
        }));

        if (!isSuccess) {
            try {
                vectorStoreRepository.deleteByMdStorageId(id);
            } catch (Exception cleanupEx) {
                log.error("cleanup vector store failed, id={}", id, cleanupEx);
            }
            aiCustomerServiceMdStorageMapper.updateById(AiCustomerServiceMdStorageDO.builder()
                    .id(id)
                    .status(AiCustomerServiceMdStatusEnum.FAILED.getCode())
                    .updateTime(LocalDateTime.now())
                    .build());
        }
    }

    private boolean hasVectorizableText(Document document) {
        return document != null && StringUtils.isNotBlank(document.getText());
    }

    private void addDocumentsInBatches(Long id, List<Document> documents) {
        for (int start = 0; start < documents.size(); start += EMBEDDING_BATCH_LIMIT) {
            int end = Math.min(start + EMBEDDING_BATCH_LIMIT, documents.size());
            List<Document> batch = List.copyOf(documents.subList(start, end));
            vectorStore.add(batch);
            log.info("markdown embedding batch added, id={}, batch={}/{}, batchSize={}",
                    id,
                    (start / EMBEDDING_BATCH_LIMIT) + 1,
                    (documents.size() + EMBEDDING_BATCH_LIMIT - 1) / EMBEDDING_BATCH_LIMIT,
                    batch.size());
        }
    }
}
