package com.zhouyuanzhi.ai.robot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "ark")
public class ArkClientProperties {

    /**
     * API Key 模式；优先使用 apiKey，其次 ak/sk。
     */
    private String apiKey;
    private String ak;
    private String sk;

    /**
     * 基础地址，默认为火山引擎北京地域。
     */
    private String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";

    /**
     * 超时（秒）与重试次数。
     */
    private long timeoutSeconds = 1800;
    private long connectTimeoutSeconds = 20;
    private int retryTimes = 2;

    /**
     * 已接入的模型列表；key 为前端传递的 modelName，value 为具体的 Endpoint/ModelId 与自定义 Header。
     */
    private Map<String, ModelConfig> models = new HashMap<>();

    @Data
    public static class ModelConfig {
        /**
         * 火山引擎模型/推理接入点 ID，例如 deepseek-v3-2-251201。
         */
        private String modelId;
        /**
         * 需要透传的额外请求头，如 X-Client-Request-Id、x-is-encrypted。
         */
        private Map<String, String> extraHeaders = new HashMap<>();
    }
}
