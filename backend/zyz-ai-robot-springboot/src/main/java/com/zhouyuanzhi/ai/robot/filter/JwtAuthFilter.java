package com.zhouyuanzhi.ai.robot.filter;

import com.zhouyuanzhi.ai.robot.config.AuthProperties;
import com.zhouyuanzhi.ai.robot.config.JwtProperties;
import com.zhouyuanzhi.ai.robot.security.JwtValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final AuthProperties authProperties;
    private final JwtProperties jwtProperties;
    private final JwtValidator jwtValidator;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public JwtAuthFilter(AuthProperties authProperties,
                         JwtProperties jwtProperties,
                         JwtValidator jwtValidator) {
        this.authProperties = authProperties;
        this.jwtProperties = jwtProperties;
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // skip OPTIONS and whitelist
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(jwtProperties.getTokenHeaderKey());
        String token = extractToken(header);
        if (!StringUtils.hasText(token)) {
            unauthorized(response, "Missing token");
            return;
        }

        try {
            Claims claims = jwtValidator.parse(token);
            String username = claims.getSubject();
            if (!StringUtils.hasText(username)) {
                unauthorized(response, "Invalid token subject");
                return;
            }
            // Propagate user for downstream use
            request.setAttribute("X-User-Name", username);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Token validation failed for {} : {}", path, ex.getMessage());
            unauthorized(response, "Token invalid or expired");
        }
    }

    private boolean isWhitelisted(String path) {
        List<String> whitelist = authProperties.getWhitelist();
        if (whitelist == null || whitelist.isEmpty()) {
            return false;
        }
        return whitelist.stream().anyMatch(pattern -> matcher.match(pattern, path));
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

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(("{\"code\":\"UNAUTHORIZED\",\"message\":\"" + message + "\"}")
                .getBytes(StandardCharsets.UTF_8));
    }
}
