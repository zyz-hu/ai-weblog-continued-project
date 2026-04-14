package com.zyz.weblog.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT issuer, must match the value used when issuing tokens.
     */
    private String issuer;

    /**
     * Base64 encoded secret used to sign/verify tokens.
     */
    private String secret;

    /**
     * Request header that carries the token.
     */
    private String tokenHeaderKey = "Authorization";

    /**
     * Prefix in the auth header, e.g. "Bearer".
     */
    private String tokenPrefix = "Bearer";

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTokenHeaderKey() {
        return tokenHeaderKey;
    }

    public void setTokenHeaderKey(String tokenHeaderKey) {
        this.tokenHeaderKey = tokenHeaderKey;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }
}
