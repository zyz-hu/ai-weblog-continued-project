package com.zhouyuanzhi.ai.robot.strategy.model;

import com.zhouyuanzhi.ai.robot.model.vo.chat.AIResponse;
import com.zhouyuanzhi.ai.robot.strategy.extractor.ReasoningExtractor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AIModelStrategy {

    boolean supports(String modelName);

    ChatClient.ChatClientRequestSpec createRequest(String modelName, String userMessage, Double temperature);

    /**
     * 获取该模型对应的思考提取器
     * @return 提取器实例
     */
    ReasoningExtractor getReasoningExtractor();

    /**
     * 默认实现：使用 ChatClient 开启流式输出。
     * 特殊模型（如官方 SDK 调用）可覆盖此方法自定义流式逻辑。
     */
    default Flux<AIResponse> streamResponse(String modelName, String userMessage, Double temperature, List<Advisor> advisors) {
        ChatClient.ChatClientRequestSpec requestSpec = createRequest(modelName, userMessage, temperature);
        if (advisors != null && !advisors.isEmpty()) {
            requestSpec.advisors(advisors);
        }
        return requestSpec
                .stream()
                .chatResponse()
                .mapNotNull(this::convertResponse);
    }

    /**
     * 将底层的 ChatResponse 转换为前端需要的 VO
     * 支持不同模型返回不同结构数据的逻辑解耦
     */
    AIResponse convertResponse(ChatResponse response);
}
