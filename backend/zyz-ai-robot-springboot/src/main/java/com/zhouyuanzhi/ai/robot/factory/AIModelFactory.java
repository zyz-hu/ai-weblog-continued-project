package com.zhouyuanzhi.ai.robot.factory;

import com.zhouyuanzhi.ai.robot.strategy.model.AIModelStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AIModelFactory {

    private final List<AIModelStrategy> strategies;

    // Spring 会自动注入所有实现了 AIModelStrategy 的 Bean 到这个 List 中
    public AIModelFactory(List<AIModelStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * 获取对应的策略
     */
    public AIModelStrategy getStrategy(String modelName) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(modelName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持或未配置的 AI 模型: " + modelName));
    }
}