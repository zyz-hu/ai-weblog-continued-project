package com.zyz.weblog.gateway.filter;

import com.zyz.weblog.gateway.config.AuthProperties;
import com.zyz.weblog.gateway.config.JwtProperties;
import com.zyz.weblog.gateway.security.JwtValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Global auth filter at gateway level. Validates JWT and propagates user info to downstream services.
 */
@Component
public class GatewayAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GatewayAuthFilter.class);

    private final AuthProperties authProperties;
    private final JwtProperties jwtProperties;
    private final JwtValidator jwtValidator;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public GatewayAuthFilter(AuthProperties authProperties, JwtProperties jwtProperties, JwtValidator jwtValidator) {
        this.authProperties = authProperties;
        this.jwtProperties = jwtProperties;
        this.jwtValidator = jwtValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        // Trace that the request passed through the gateway auth filter.
        log.info("GatewayAuthFilter handling path={} method={}", path, request.getMethod());

        // Preflight and whitelisted paths bypass auth.
        if (HttpMethod.OPTIONS.equals(request.getMethod()) || isWhitelisted(path) || !isProtectedPath(path)) {
            return chain.filter(exchange);
        }

        String rawHeader = request.getHeaders().getFirst(jwtProperties.getTokenHeaderKey());
        String token = extractToken(rawHeader);
        if (!StringUtils.hasText(token)) {
            log.debug("Missing token for path {}", path);
            return unauthorized(exchange, "UNAUTHORIZED", "Missing token");
        }

        try {
            Claims claims = jwtValidator.parse(token);
            String username = claims.getSubject();
            if (!StringUtils.hasText(username)) {
                return unauthorized(exchange, "UNAUTHORIZED", "Invalid token subject");
            }

            // Propagate user identity to downstream services.
            ServerHttpRequest mutated = request.mutate()
                    .header("X-User-Name", username)
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Token validation failed for path {}: {}", path, ex.getMessage());
            return unauthorized(exchange, "UNAUTHORIZED", "Token invalid or expired");
        }
    }

    @Override
    public int getOrder() {
        // Run early so auth happens before other filters.
        return -100;
    }

    private boolean isWhitelisted(String path) {
        List<String> whitelist = authProperties.getWhitelist();
        if (Objects.isNull(whitelist)) {
            return false;
        }
        return whitelist.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private boolean isProtectedPath(String path) {
        List<String> securePaths = authProperties.getSecurePaths();
        if (Objects.isNull(securePaths) || securePaths.isEmpty()) {
            // Protect everything except whitelist when not configured.
            return true;
        }
        return securePaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private String extractToken(String header) {
        if (!StringUtils.hasText(header)) {
            return null;
        }
        String prefix = jwtProperties.getTokenPrefix();
        if (StringUtils.hasText(prefix)) {
            if (!header.startsWith(prefix)) {
                return null;
            }
            return header.substring(prefix.length()).trim();
        }
        return header.trim();
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String code, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"code\":\"%s\",\"message\":\"%s\"}", code, message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
