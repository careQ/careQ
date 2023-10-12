package com.reve.careQ.global.security.userInfo;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;
    private Map<String, Object> attributesResponse;
    // private Map<String, Object> attributesProperties; -> nickname 설정 처리

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.attributesResponse = (Map<String, Object>) attributes.get("kakao_account");
    // this.attributesProperties = (Map<String, Object>) attributes.get("properties"); -> nickname 설정 처리
    }

    @Override
    public String getProviderId() {
        return attributes.get("id") != null ? attributes.get("id").toString() : null;
    }

    @Override
    public String getProviderTypeCode() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        return attributesResponse.get("email").toString();
    }

    @Override
    public String getUsername() {
        return attributes.get("name").toString();
    }

}
