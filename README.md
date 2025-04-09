## 프로젝트 개요
Spring Boot 기반 OAuth 인증 및 JWT 토큰 발급 시스템입니다.
- 소셜 로그인(Kakao, Google) 지원
- 자체 회원가입/로그인
- Access/Refresh Token 발급 및 Redis 저장
- JWT 기반 Stateless 인증 처리


## 기술 스택
- Java 17
- Spring Boot 3.x
- Spring Security
- JWT (jjwt)
- Redis (Lettuce)
- MySQL
- Gradle


## 디렉토리 구조
📁 주요 디렉토리 구조 (DDD + 기능 기반 분리)
```
src/main/java/com/example/oauth
├── controller/             # AuthController, OAuthController, TokenController
├── domain/
│   ├── member/             # 사용자 도메인 (Entity, Repository, Service)
│   ├── oauth/              # Google/Kakao 연동 서비스
│   └── token/              # RefreshToken Redis Entity/Repo/Service
├── common/
│   ├── auth/               # JwtTokenProvider, JwtTokenFilter
│   └── config/             # SecurityConfig, RedisConfig
```


## 환경 설정 (application.yml 예시)
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/oauthdb?useSSL=false
    username: oauthuser
    password: oauthpass
  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect
    hibernate:
      ddl-auto: create
  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: <jwt-secret-base64>
  expiration: 3000 # in minutes

oauth:
  google:
    client-id: <google-client-id>
    client-secret: <google-client-secret>
    redirect-uri: http://localhost:3000/oauth/google/redirect
  kakao:
    client-id: <kakao-client-id>
    redirect-uri: http://localhost:3000/oauth/kakao/redirect
```


## 기능 요약
| 기능 구분       | 설명 |
|----------------|------|
| 회원가입        | `/auth/signup` |
| 로그인          | `/auth/login` → JWT 발급 |
| 로그아웃        | `/auth/logout` → RefreshToken 삭제 |
| 토큰 재발급     | `/auth/token/refresh` |
| Google 로그인   | `/oauth/google/login` |
| Kakao 로그인    | `/oauth/kakao/login` |


## JWT 인증 흐름 요약
1. 로그인/소셜 로그인 성공 시 AccessToken + RefreshToken 발급
2. AccessToken은 요청 헤더 `Authorization: Bearer <token>`으로 전달
3. RefreshToken은 Redis에 저장
4. AccessToken 만료 시 `/auth/token/refresh`로 갱신
5. 로그아웃 시 RefreshToken 삭제


## 테스트
- `RefreshTokenServiceTest`: Redis 저장/조회/삭제 테스트
- 추후 OAuthController, AuthController에 대한 MockMvc 테스트 확장 가능


## 실행 방법
```bash
./gradlew bootRun
```

또는 테스트:

```bash
./gradlew test
```

Redis 실행 (Docker):

```bash
docker run -d -p 6379:6379 --name redis redis:latest
```
