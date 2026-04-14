package com.zyz.weblog.gateway.security;

import com.zyz.weblog.gateway.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.util.Base64;

@Component
public class JwtValidator {

    private final JwtProperties jwtProperties;
    private JwtParser jwtParser;

    public JwtValidator(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        Assert.hasText(jwtProperties.getSecret(), "jwt.secret must not be blank");
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.getSecret()));
        jwtParser = Jwts.parser()
                .requireIssuer(jwtProperties.getIssuer())
                .verifyWith(key)
                .setAllowedClockSkewSeconds(10)
                .build();
    }

    public Claims parse(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }
}
