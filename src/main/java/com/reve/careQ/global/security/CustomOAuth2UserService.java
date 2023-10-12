package com.reve.careQ.global.security;

import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.global.security.userInfo.GoogleUserInfo;
import com.reve.careQ.global.security.userInfo.KakaoUserInfo;
import com.reve.careQ.global.security.userInfo.NaverUserInfo;
import com.reve.careQ.global.security.userInfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberService memberService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        String providerTypeCode = userRequest.getClientRegistration().getRegistrationId();

        if(providerTypeCode.equals("google")){
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }
        else if(providerTypeCode.equals("naver")){
            oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
        }
        else if (providerTypeCode.equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

        }

        String providerId = oAuth2UserInfo.getProviderId();
        String username = providerTypeCode+"_"+providerId;
        String email = oAuth2UserInfo.getEmail();

        Member member = memberService.whenSocialLogin(providerTypeCode, username, email).getData();

        return new SecurityMember(member.getId(), member.getUsername(), member.getPassword(), member.getGrantedAuthorities());

    }
}
