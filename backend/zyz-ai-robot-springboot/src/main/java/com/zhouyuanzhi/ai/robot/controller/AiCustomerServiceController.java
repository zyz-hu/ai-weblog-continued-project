package com.zhouyuanzhi.ai.robot.controller;

import com.zhouyuanzhi.ai.robot.aspect.ApiOperationLog;
import com.zhouyuanzhi.ai.robot.model.vo.chat.AIResponse;
import com.zhouyuanzhi.ai.robot.model.vo.knowledge.KnowledgeChatReqVO;
import com.zhouyuanzhi.ai.robot.model.vo.knowledge.KnowledgeFilePageReqVO;
import com.zhouyuanzhi.ai.robot.model.vo.knowledge.KnowledgeFilePageRspVO;
import com.zhouyuanzhi.ai.robot.model.vo.knowledge.KnowledgeFileUpdateReqVO;
import com.zhouyuanzhi.ai.robot.service.CustomerService;
import com.zhouyuanzhi.ai.robot.utils.PageResponse;
import com.zhouyuanzhi.ai.robot.utils.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * 私有知识库与智能客服
 */
@RestController
@RequestMapping("/customer-service")
@Validated
@RequiredArgsConstructor
public class AiCustomerServiceController {

    private final CustomerService customerService;

    @PostMapping(value = "/md/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperationLog(description = "上传知识库 Markdown 文件")
    public Response<?> uploadMarkdownFile(@RequestPart(value = "file", required = false) MultipartFile file,
                                          @RequestPart(value = "remark", required = false) String remark) {
        return customerService.uploadMarkdownFile(file, remark);
    }

    @GetMapping("/md/page")
    @ApiOperationLog(description = "分页查询知识库文件")
    public PageResponse<KnowledgeFilePageRspVO> pageKnowledgeFiles(KnowledgeFilePageReqVO reqVO) {
        return customerService.pageKnowledgeFiles(reqVO);
    }

    @PutMapping(value = "/md/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperationLog(description = "更新知识库文件")
    public Response<?> updateKnowledgeFile(@RequestPart("id") Long id,
                                           @RequestPart(value = "remark", required = false) String remark,
                                           @RequestPart(value = "file", required = false) MultipartFile file) {
        KnowledgeFileUpdateReqVO reqVO = new KnowledgeFileUpdateReqVO();
        reqVO.setId(id);
        reqVO.setRemark(remark);
        return customerService.updateKnowledgeFile(reqVO, file);
    }

    @DeleteMapping("/md/{id}")
    @ApiOperationLog(description = "删除知识库文件")
    public Response<?> deleteKnowledgeFile(@PathVariable("id") Long id) throws IOException {
        return customerService.deleteKnowledgeFile(id);
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperationLog(description = "知识库 RAG 智能客服（流式）")
    public Flux<AIResponse> knowledgeChat(@RequestBody @Validated KnowledgeChatReqVO reqVO) {
        return customerService.knowledgeChat(reqVO);
    }
}
