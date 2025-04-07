package com.example.oauth.domain.oauth.service;

import com.example.oauth.domain.oauth.dto.AccessTokenDto;
import com.example.oauth.domain.oauth.dto.KakaoProfileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoService {

    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    public AccessTokenDto getAccessToken(String code) {
        RestClient restClient = RestClient.create();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("grant_type", "authorization_code");

        ResponseEntity<AccessTokenDto> response = restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(params)
                .retrieve()
                .toEntity(AccessTokenDto.class);

        System.out.println("Data: "+ response.getBody());

        return response.getBody();
    }

    public KakaoProfileDto getKakaoProfile(String accessToken) {
        RestClient restClient = RestClient.create();

        ResponseEntity<KakaoProfileDto> response = restClient.post()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(KakaoProfileDto.class);

        System.out.println("Profile JSON " + response.getBody());

        return response.getBody();
    }
}
