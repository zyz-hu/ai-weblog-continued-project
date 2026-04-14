package com.zhouyuanzhi.ai.robot.service;

import com.zhouyuanzhi.ai.robot.model.vo.chat.AIResponse;
import com.zhouyuanzhi.ai.robot.model.vo.knowledge.KnowledgeChatReqVO;
import com.zhouyuanzhi.ai.robot.model.vo.knowledge.KnowledgeFilePageReqVO;
import com.zhouyuanzhi.ai.robot.model.vo.knowledge.KnowledgeFilePageRspVO;
import com.zhouyuanzhi.ai.robot.model.vo.knowledge.KnowledgeFileUpdateReqVO;
import com.zhouyuanzhi.ai.robot.utils.PageResponse;
import com.zhouyuanzhi.ai.robot.utils.Response;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * 私有知识库接口
 */
public interface CustomerService {

    Response<?> uploadMarkdownFile(MultipartFile file, String remark);

    PageResponse<KnowledgeFilePageRspVO> pageKnowledgeFiles(KnowledgeFilePageReqVO reqVO);

    Response<?> updateKnowledgeFile(KnowledgeFileUpdateReqVO reqVO, MultipartFile file);

    Response<?> deleteKnowledgeFile(Long id) throws IOException;

    Flux<AIResponse> knowledgeChat(KnowledgeChatReqVO reqVO);
}
