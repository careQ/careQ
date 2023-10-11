package com.reve.careQ.global.security.userInfo;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProviderTypeCode();
    String getEmail();
    String getUsername();
}
