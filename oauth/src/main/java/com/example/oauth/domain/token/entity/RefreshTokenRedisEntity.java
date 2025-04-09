package com.example.oauth.domain.token.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 7) // 7일 TTL
public class RefreshTokenRedisEntity {

    @Id
    private String email;
    private String token;
}
