package com.zyz.weblog.auth.controller;

import com.zyz.weblog.auth.config.AuthProperties;
import com.zyz.weblog.auth.model.ApiResponse;
import com.zyz.weblog.auth.model.IntrospectResponse;
import com.zyz.weblog.auth.model.LoginRequest;
import com.zyz.weblog.auth.model.LogoutRequest;
import com.zyz.weblog.auth.model.RefreshRequest;
import com.zyz.weblog.auth.model.TokenResponse;
import com.zyz.weblog.auth.model.UserInfoResponse;
import com.zyz.weblog.auth.entity.User;
import com.zyz.weblog.auth.service.JwtService;
import com.zyz.weblog.auth.service.TokenBlacklistService;
import com.zyz.weblog.auth.service.UserAuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jws;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserAuthService userAuthService;
    private final JwtService jwtService;
    private final AuthProperties properties;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(UserAuthService userAuthService,
                          JwtService jwtService,
                          AuthProperties properties,
                          TokenBlacklistService tokenBlacklistService) {
        this.userAuthService = userAuthService;
        this.jwtService = jwtService;
        this.properties = properties;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        Optional<User> user = userAuthService.validateCredentials(request.getUsername(), request.getPassword());
        if (user.isEmpty()) {
            return ApiResponse.fail("Invalid username or password");
        }
        List<String> roles = userAuthService.extractRoleCodes(user.get());
        TokenResponse tokenResponse = buildTokens(request.getUsername(), roles);
        return ApiResponse.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@RequestBody @Valid RefreshRequest refreshRequest) {
        try {
            Jws<Claims> claims = jwtService.parse(refreshRequest.getRefreshToken());
            if (tokenBlacklistService.isBlacklisted(claims.getBody().getId())) {
                return ApiResponse.fail("Refresh token is revoked");
            }
            if (!jwtService.isTokenType(claims, "refresh")) {
                return ApiResponse.fail("Invalid refresh token");
            }
            String username = claims.getBody().getSubject();
            List<String> roles = claims.getBody().get("roles", List.class);
            TokenResponse tokenResponse = buildTokens(username, roles);
            return ApiResponse.ok(tokenResponse);
        } catch (JwtException e) {
            return ApiResponse.fail("Refresh token invalid or expired");
        }
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody(required = false) LogoutRequest body,
                                                      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        String token = resolveToken(body != null ? body.getToken() : null, authorizationHeader);
        IntrospectResponse resp = new IntrospectResponse();
        if (StringUtils.isBlank(token)) {
            resp.setActive(false);
            return ApiResponse.ok(resp);
        }
        try {
            Jws<Claims> claims = jwtService.parse(token);
            if (tokenBlacklistService.isBlacklisted(claims.getBody().getId())) {
                resp.setActive(false);
                return ApiResponse.ok(resp);
            }
            resp.setActive(true);
            resp.setSubject(claims.getBody().getSubject());
            resp.setTokenType(claims.getBody().get("typ", String.class));
            resp.setIssuedAt(claims.getBody().getIssuedAt().toInstant());
            resp.setExpiresAt(claims.getBody().getExpiration().toInstant());
            resp.setRoles(claims.getBody().get("roles", List.class));
            return ApiResponse.ok(resp);
        } catch (ExpiredJwtException e) {
            resp.setActive(false);
            resp.setExpiresAt(Optional.ofNullable(e.getClaims().getExpiration()).map(Date::toInstant).orElse(null));
            return ApiResponse.ok(resp);
        } catch (JwtException e) {
            resp.setActive(false);
            return ApiResponse.ok(resp);
        }
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody(required = false) LogoutRequest body,
                                    @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        // Placeholder: add token to blacklist / revoke list if persistence is available.
        String token = resolveToken(body != null ? body.getToken() : null, authorizationHeader);
        if (StringUtils.isBlank(token)) {
            return ApiResponse.fail("Missing token");
        }
        try {
            Jws<Claims> claims = jwtService.parse(token);
            tokenBlacklistService.blacklist(claims.getBody().getId(), claims.getBody().getExpiration().toInstant());
        } catch (JwtException ignored) {
        }
        return ApiResponse.ok();
    }

    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> me(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        String token = resolveToken(null, authorizationHeader);
        if (StringUtils.isBlank(token)) {
            return ApiResponse.fail("Missing token");
        }
        try {
            Jws<Claims> claims = jwtService.parse(token);
            if (tokenBlacklistService.isBlacklisted(claims.getBody().getId())) {
                return ApiResponse.fail("Token revoked");
            }
            String username = claims.getBody().getSubject();
            Optional<User> user = userAuthService.findActiveUser(username);
            if (user.isEmpty()) {
                return ApiResponse.fail("User not found");
            }
            List<String> roles = userAuthService.extractRoleCodes(user.get());
            List<String> permissions = userAuthService.extractPermissionResources(user.get());
            return ApiResponse.ok(new UserInfoResponse(username, roles, permissions));
        } catch (JwtException e) {
            return ApiResponse.fail("Invalid token");
        }
    }

    private TokenResponse buildTokens(String username, List<String> roles) {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(jwtService.generateAccessToken(username, roles));
        tokenResponse.setAccessTokenExpiresIn(properties.getAccessTokenTtlMinutes() * 60);
        tokenResponse.setRefreshToken(jwtService.generateRefreshToken(username, roles));
        tokenResponse.setRefreshTokenExpiresIn(properties.getRefreshTokenTtlMinutes() * 60);
        return tokenResponse;
    }

    private String resolveToken(String token, String authHeader) {
        if (StringUtils.isNotBlank(token)) {
            return token.trim();
        }
        if (StringUtils.startsWithIgnoreCase(authHeader, "Bearer ")) {
            return authHeader.substring("Bearer ".length()).trim();
        }
        return null;
    }
}
