# Weblog Auth Service (standalone)

Spring Boot 3.2.5, JDK 21. H2 in-memory DB (auto DDL + seed users/roles/permissions).

## Run
```bash
cd backend/auth-service
mvn spring-boot:run
```
Default port `9000`. Seed users:
- admin / admin123 (ROLE_ADMIN)
- user / user123 (ROLE_USER)

## Endpoints
- `POST /auth/login` `{username,password}` -> accessToken + refreshToken
- `POST /auth/refresh` `{refreshToken}` -> new access/refresh
- `POST /auth/introspect` `{token}` or `Authorization: Bearer xxx` -> token status (roles, exp)
- `POST /auth/logout` `{token}` -> add token jti to blacklist (in-memory; swap to Redis for prod)
- `GET /auth/me` -> user info + roles + permission resources

## Config
`src/main/resources/application.yml`
- `auth.secret`: Base64 HS256 key (replace for prod)  
- `auth.access-token-ttl-minutes` / `auth.refresh-token-ttl-minutes`  
- `auth.issuer`: token issuer

## Notes for integration
- Gateway validates JWT (cache key), then forward identity headers such as `X-User-Name`, `X-Roles`, `X-Permissions`.
- For production: change DB source, move blacklist to Redis/DB, consider RS256 with public key distribution.
