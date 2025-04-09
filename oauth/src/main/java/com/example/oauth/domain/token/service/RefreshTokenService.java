package com.example.oauth.domain.token.service;

import com.example.oauth.domain.token.entity.RefreshTokenRedisEntity;
import com.example.oauth.domain.token.repository.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRedisRepository redisRepository;

    public void saveOrUpdate(String email, String token) {
        RefreshTokenRedisEntity entity = RefreshTokenRedisEntity.builder()
                .email(email)
                .token(token)
                .build();
        redisRepository.save(entity);
    }

    public boolean validate(String email, String token) {
        return redisRepository.findById(email)
                .map(rt -> rt.getToken().equals(token))
                .orElse(false);
    }

    public void delete(String email) {
        redisRepository.deleteById(email);
    }
}
