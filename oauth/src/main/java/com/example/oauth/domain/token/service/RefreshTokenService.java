package com.example.oauth.domain.token.service;

import com.example.oauth.domain.token.entity.RefreshToken;
import com.example.oauth.domain.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveOrUpdate(String email, String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByEmail(email)
                .map(rt -> {
                    rt.updateToken(token);
                    return rt;
                })
                .orElse(RefreshToken.builder().email(email).token(token).build());
        refreshTokenRepository.save(refreshToken);
    }

    public boolean validate(String email, String token) {
        return refreshTokenRepository.findByEmail(email)
                .map(rt -> rt.getToken().equals(token))
                .orElse(false);
    }

    public void delete(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }
}
