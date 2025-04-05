package com.example.oauth.member.controller;

import com.example.oauth.common.auth.JwtTokenProvider;
import com.example.oauth.member.domain.Member;
import com.example.oauth.member.domain.SocialType;
import com.example.oauth.member.dto.*;
import com.example.oauth.member.service.GoogleService;
import com.example.oauth.member.service.KakaoService;
import com.example.oauth.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleService googleService;
    private final KakaoService kakaoService;

    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider, GoogleService googleService, KakaoService kakaoService) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.googleService = googleService;
        this.kakaoService = kakaoService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberCreateDto memberCreateDto) {
        Member member = memberService.create(memberCreateDto);
        return new ResponseEntity<>(member.getId(), HttpStatus.CREATED);
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginDto memberLoginDto) {
        Member member = memberService.login(memberLoginDto);

        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    @PostMapping("/google/doLogin")
    public ResponseEntity<?> googleLogin(@RequestBody RedirectDto redirectDto){
        // 액세스 토큰 발급
        AccessTokenDto accessTokenDto = googleService.getAccessToken(redirectDto.getCode());
        // 사용자 정보 획득
        GoogleProfileDto googleProfileDto = googleService.getGoogleProfile(accessTokenDto.getAccess_token());
        // 신규 로그인   회원가입 처리
        Member originalMember = memberService.getMemberBySocialId(googleProfileDto.getSub());
        if(originalMember == null) {
            originalMember = memberService.createOauth(googleProfileDto.getSub(), googleProfileDto.getEmail(), SocialType.GOOGLE);
        }
        // 서버의 JWT 토큰 발급
        String jwtToken = jwtTokenProvider.createToken(originalMember.getEmail(), originalMember.getRole().toString());

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getId());
        loginInfo.put("token", jwtToken);
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
    @PostMapping("/kakao/doLogin")
    public ResponseEntity<?> kakaoLogin(@RequestBody RedirectDto redirectDto) {
        // 액세스 코드
        AccessTokenDto accessTokenDto = kakaoService.getAccessToken(redirectDto.getCode());
        // 사용자 정보 획득
        KakaoProfileDto kakaoProfileDto = kakaoService.getKakaoProfile(accessTokenDto.getAccess_token());

        Member originalMember = memberService.getMemberBySocialId(kakaoProfileDto.getId());
        if(originalMember == null) {
            originalMember = memberService.createOauth(kakaoProfileDto.getId(), kakaoProfileDto.getKakao_account().getEmail(), SocialType.KAKAO);
        }
        // 서버의 JWT 토큰 발급
        String jwtToken = jwtTokenProvider.createToken(originalMember.getEmail(), originalMember.getRole().toString());

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getId());
        loginInfo.put("token", jwtToken);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }
}
