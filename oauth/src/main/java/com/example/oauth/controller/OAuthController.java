package com.example.oauth.controller;

import com.example.oauth.common.auth.JwtTokenProvider;
import com.example.oauth.domain.member.entity.Member;
import com.example.oauth.domain.member.entity.SocialType;
import com.example.oauth.domain.member.service.MemberService;
import com.example.oauth.domain.oauth.dto.RedirectDto;
import com.example.oauth.domain.oauth.service.GoogleService;
import com.example.oauth.domain.oauth.service.KakaoService;
import com.example.oauth.domain.token.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private final GoogleService googleService;
    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/google/login")
    public ResponseEntity<?> googleLogin(@RequestBody RedirectDto dto) {
        var accessTokenDto = googleService.getAccessToken(dto.getCode());
        var googleProfileDto = googleService.getGoogleProfile(accessTokenDto.getAccess_token());

        Member originalMember = memberService.getMemberBySocialId(googleProfileDto.getSub());
        if(originalMember == null) {
            originalMember = memberService.createOauth(
                    googleProfileDto.getSub(),
                    googleProfileDto.getEmail(),
                    SocialType.GOOGLE);
        }

        // 서버의 JWT 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(originalMember.getEmail(), originalMember.getRole().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(originalMember.getEmail());
        refreshTokenService.saveOrUpdate(originalMember.getEmail(), refreshToken);

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getId());
        loginInfo.put("accessToken", accessToken);
        loginInfo.put("refreshToken", refreshToken);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    /*
     * ✅ Kakao OAuth 토큰 응답 예시 (HTTP 200 OK)
     * ------------------------------------------------------
     * 📌 응답 본문 (Body):
     * {
     *   "access_token": "d3k_uG6DwmUds6VoYvnKqdC1hfgfIf_rAAAAAQoNFdgAAAGWA50s3Yh6dPOEuoNF",
     *   "token_type": "bearer",
     *   "refresh_token": "g-LP-OKZf9lgrdevqF43SXHmWOK61V3sAAAAAgoNFdgAAAGWA50s1oh6dPOEuoNF",
     *   "expires_in": 21599,
     *   "scope": "account_email",
     *   "refresh_token_expires_in": 5183999
     * }
     * ------------------------------------------------------
     */
    @PostMapping("/kakao/login")
    public ResponseEntity<?> kakaoLogin(@RequestBody RedirectDto dto) {
        var accessTokenDto = kakaoService.getAccessToken(dto.getCode());
        var kakaoProfileDto = kakaoService.getKakaoProfile(accessTokenDto.getAccess_token());

        Member originalMember = memberService.getMemberBySocialId(kakaoProfileDto.getId());
        if(originalMember == null) {
            originalMember = memberService.createOauth(
                    kakaoProfileDto.getId(),
                    kakaoProfileDto.getKakao_account().getEmail(),
                    SocialType.KAKAO);
        }

        String accessToken = jwtTokenProvider.createAccessToken(
                originalMember.getEmail(),
                originalMember.getRole().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(originalMember.getEmail());
        refreshTokenService.saveOrUpdate(originalMember.getEmail(), refreshToken);

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getId());
        loginInfo.put("accessToken", accessToken);
        loginInfo.put("refreshToken", refreshToken);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }
}
