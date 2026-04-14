package com.zyz.weblog.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * Paths that skip authentication entirely.
     */
    private List<String> whitelist = new ArrayList<>(Arrays.asList(
            "/login",
            "/actuator/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/favicon.ico"
    ));

    /**
     * Paths that must be authenticated. If empty, all non-whitelisted paths are protected.
     */
    private List<String> securePaths = new ArrayList<>(Arrays.asList(
            "/admin/**",
            "/robot/**",
            "/chat/**"
    ));

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

    public List<String> getSecurePaths() {
        return securePaths;
    }

    public void setSecurePaths(List<String> securePaths) {
        this.securePaths = securePaths;
    }
}
