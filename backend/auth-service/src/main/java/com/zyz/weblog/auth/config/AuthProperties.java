package com.zyz.weblog.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * JWT issuer name.
     */
    private String issuer = "weblog-auth";

    /**
     * Base64 encoded secret used to sign/verify tokens (HS256).
     */
    private String secret;

    /**
     * Access token validity in minutes.
     */
    private long accessTokenTtlMinutes = 60;

    /**
     * Refresh token validity in minutes.
     */
    private long refreshTokenTtlMinutes = 7 * 24 * 60;

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

    public long getAccessTokenTtlMinutes() {
        return accessTokenTtlMinutes;
    }

    public void setAccessTokenTtlMinutes(long accessTokenTtlMinutes) {
        this.accessTokenTtlMinutes = accessTokenTtlMinutes;
    }

    public long getRefreshTokenTtlMinutes() {
        return refreshTokenTtlMinutes;
    }

    public void setRefreshTokenTtlMinutes(long refreshTokenTtlMinutes) {
        this.refreshTokenTtlMinutes = refreshTokenTtlMinutes;
    }
}
