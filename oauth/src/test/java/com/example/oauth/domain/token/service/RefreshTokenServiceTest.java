package com.example.oauth.domain.token.service;

import com.example.oauth.domain.token.repository.RefreshTokenRedisRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RefreshTokenServiceTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRedisRepository redisRepository;

    private final String email = "jsmini3814@gmail.com";
    private final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqc21pbmkzODE0QGdtYWlsLmNvbSIsInJvbGUiOiJSRUZSRVNIIiwiaWF0IjoxNzQ0MjEzMjUxLCJleHAiOjE3NDQ4MTgwNTF9.eV-Vk9KH0DZdNvhQOwOQww3AxERh7qBck1wnAYcwRq0";

    @AfterEach
    void tearDown() {
        redisRepository.deleteById(email);
    }

    @Test
    @DisplayName("Redis에 리프레시 토큰을 저장하고 검증한다")
    void saveAndValidate() {
        refreshTokenService.saveOrUpdate(email, token);

        boolean result = refreshTokenService.validate(email, token);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("저장된 리프레시 토큰을 삭제한다")
    void deleteToken() {
        refreshTokenService.saveOrUpdate(email, token);
        refreshTokenService.delete(email);

        boolean result = refreshTokenService.validate(email, token);
        assertThat(result).isFalse();
    }
}
