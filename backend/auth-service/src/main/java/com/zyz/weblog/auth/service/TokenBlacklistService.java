package com.zyz.weblog.auth.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory token blacklist based on jti + expiration.
 * In production replace with persistent/shared store (e.g., Redis).
 */
@Service
public class TokenBlacklistService {

    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String jti, Instant expiresAt) {
        if (jti != null && expiresAt != null) {
            blacklist.put(jti, expiresAt);
        }
    }

    public boolean isBlacklisted(String jti) {
        if (jti == null) {
            return false;
        }
        Instant exp = blacklist.get(jti);
        if (exp == null) {
            return false;
        }
        // cleanup expired entries
        if (exp.isBefore(Instant.now())) {
            blacklist.remove(jti);
            return false;
        }
        return true;
    }
}
