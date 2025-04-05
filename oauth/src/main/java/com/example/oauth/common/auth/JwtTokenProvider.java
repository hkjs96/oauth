package com.example.oauth.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String secretKey;

    private final int expiration;

    private Key SECRET_KEY;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") int expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        // SHA-512 사용
        this.SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        //this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
    }

    public String createToken(String email, String role) {
        // claims 은 jwt 토큰의 payload 부분
        // https://github.com/jwtk/jjwt?tab=readme-ov-file#creating-a-jwt
        Claims claims = Jwts.claims()
                .subject(email)
                .add("role", role)
                .build();
        Date now = new Date();
        String token = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration*60*1000L))
                .signWith(SECRET_KEY)
                .compact();
        return token;
    }
}
