package com.zyz.weblog.auth.service;

import com.zyz.weblog.auth.config.AuthProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {

    private final AuthProperties properties;
    private SecretKey key;

    public JwtService(AuthProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        Assert.hasText(properties.getSecret(), "auth.secret must not be blank");
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.getSecret()));
    }

    public String generateAccessToken(String username, List<String> roles) {
        return generateToken(username, roles, properties.getAccessTokenTtlMinutes(), "access");
    }

    public String generateRefreshToken(String username, List<String> roles) {
        return generateToken(username, roles, properties.getRefreshTokenTtlMinutes(), "refresh");
    }

    private String generateToken(String username, List<String> roles, long ttlMinutes, String type) {
        Instant now = Instant.now();
        Instant expiry = now.plus(ttlMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(username)
                .setIssuer(properties.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("typ", type)
                .claim("roles", roles)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .requireIssuer(properties.getIssuer())
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public boolean isTokenType(Jws<Claims> claims, String type) {
        return type.equalsIgnoreCase(claims.getBody().get("typ", String.class));
    }
}
