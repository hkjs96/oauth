package com.example.oauth.controller;

import com.example.oauth.common.auth.JwtTokenProvider;
import com.example.oauth.domain.member.dto.MemberCreateDto;
import com.example.oauth.domain.member.dto.MemberLoginDto;
import com.example.oauth.domain.member.service.MemberService;
import com.example.oauth.domain.token.service.RefreshTokenService;
import com.example.oauth.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberCreateDto dto) {
        Member member = memberService.create(dto);
        return new ResponseEntity<>(member.getId(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginDto dto) {
        Member member = memberService.login(dto);

        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        refreshTokenService.saveOrUpdate(member.getEmail(), refreshToken);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        if (jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getSubject(token);
            refreshTokenService.delete(email);
        }
        return ResponseEntity.ok().build();
    }
}

