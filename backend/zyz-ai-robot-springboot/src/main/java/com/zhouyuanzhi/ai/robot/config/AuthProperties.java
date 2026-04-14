package com.zhouyuanzhi.ai.robot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * Paths that bypass auth checks.
     */
    private List<String> whitelist = new ArrayList<>(Arrays.asList(
            "/robot/actuator/**",
            "/robot/swagger-ui/**",
            "/robot/swagger-resources/**",
            "/robot/v3/api-docs/**",
            "/robot/webjars/**",
            "/robot/favicon.ico"
    ));

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }
}
