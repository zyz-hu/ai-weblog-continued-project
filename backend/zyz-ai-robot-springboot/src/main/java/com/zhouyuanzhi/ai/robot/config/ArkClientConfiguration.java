package com.zhouyuanzhi.ai.robot.config;

import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(ArkClientProperties.class)
public class ArkClientConfiguration {

    @Bean
    public ArkService arkService(ArkClientProperties properties) {
        Dispatcher dispatcher = new Dispatcher();
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);

        ArkService.Builder builder = ArkService.builder()
                .baseUrl(properties.getBaseUrl())
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .connectTimeout(Duration.ofSeconds(properties.getConnectTimeoutSeconds()))
                .retryTimes(properties.getRetryTimes())
                .dispatcher(dispatcher)
                .connectionPool(connectionPool);

        if (StringUtils.hasText(properties.getApiKey())) {
            builder.apiKey(properties.getApiKey());
        } else if (StringUtils.hasText(properties.getAk()) && StringUtils.hasText(properties.getSk())) {
            builder.ak(properties.getAk()).sk(properties.getSk());
        }

        return builder.build();
    }

    @Bean(name = "arkChatModel")
    public ChatModel arkChatModel(ArkService arkService, ArkClientProperties properties) {
        return new com.zhouyuanzhi.ai.robot.model.ark.ArkChatModel(arkService, properties);
    }
}
