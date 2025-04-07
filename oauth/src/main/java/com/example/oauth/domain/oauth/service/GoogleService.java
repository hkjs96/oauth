package com.example.oauth.domain.oauth.service;

import com.example.oauth.domain.oauth.dto.AccessTokenDto;
import com.example.oauth.domain.oauth.dto.GoogleProfileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class GoogleService {

    @Value("${oauth.google.client-id}")
    private String googleClientId;

    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;

    public AccessTokenDto getAccessToken(String code) {
        // 인가코드, clientId, clientSecret, redirectUrl, GrantType

        // RestTemplate 은 스프링 6 부터는 Deprecated, RestClient 권장
        RestClient restClient = RestClient.create();

        // MultiValueMap를 통해 자동으로 form-data 형식으로 body 조립 가능
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        ResponseEntity<AccessTokenDto> response = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(params)
                // retrieve: 응답 Body 값만 추출
                .retrieve()
                .toEntity(AccessTokenDto.class);

        System.out.println("응답 Access Token JSON " + response.getBody());
        return response.getBody();
    }

    public GoogleProfileDto getGoogleProfile(String token) {
        RestClient restClient = RestClient.create();


        ResponseEntity<GoogleProfileDto> response = restClient.post()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(GoogleProfileDto.class);

        System.out.println("Profile JSON " + response.getBody());

        return response.getBody();
    }
}
