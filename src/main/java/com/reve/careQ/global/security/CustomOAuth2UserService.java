package com.reve.careQ.global.security;

import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.repository.MemberRepository;
import com.reve.careQ.global.security.userInfo.GoogleUserInfo;
import com.reve.careQ.global.security.userInfo.KakaoUserInfo;
import com.reve.careQ.global.security.userInfo.NaverUserInfo;
import com.reve.careQ.global.security.userInfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String providerTypeCode = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = createUserInfo(providerTypeCode, oAuth2User);
        Member member = saveOrUpdate(oAuth2UserInfo);
        validateProviderType(member, oAuth2UserInfo);

        return new SecurityMember(member.getId(), member.getUsername(), member.getPassword(), member.getGrantedAuthorities());
    }

    private OAuth2UserInfo createUserInfo(String providerTypeCode, OAuth2User oAuth2User) {
        switch (providerTypeCode) {
            case "google":
                return new GoogleUserInfo(oAuth2User.getAttributes());
            case "naver":
                return new NaverUserInfo(oAuth2User.getAttributes());
            case "kakao":
                return new KakaoUserInfo(oAuth2User.getAttributes());
            default:
                throw new RuntimeException("Unsupported provider type: " + providerTypeCode);
        }
    }

    private void validateProviderType(Member member, OAuth2UserInfo oAuth2UserInfo) {
        if (!"careQ".equals(member.getProviderTypeCode()) && !member.getProviderTypeCode().equals(oAuth2UserInfo.getProviderTypeCode())) {
            throw new OAuth2AuthenticationException(new OAuth2Error("ERROR"), String.format("%s", member.getProviderTypeCode()));
        }
    }

    private Member saveOrUpdate(OAuth2UserInfo oAuth2UserInfo) {
        Optional<Member> existingMember = memberRepository.findByEmail(oAuth2UserInfo.getEmail());

        return existingMember.map(member -> updateExistingMember(member, oAuth2UserInfo))
                .orElseGet(() -> createNewMember(oAuth2UserInfo));
    }

    private Member updateExistingMember(Member existingMember, OAuth2UserInfo oAuth2UserInfo) {
        if ("careQ".equals(existingMember.getProviderTypeCode())) {
            existingMember.setProviderTypeCode(oAuth2UserInfo.getProviderTypeCode());
            return memberRepository.save(existingMember);
        } else {
            return existingMember;
        }
    }

    private Member createNewMember(OAuth2UserInfo oAuth2UserInfo) {
        return memberRepository.save(Member.builder()
                .username(oAuth2UserInfo.getUsername() + UUID.randomUUID().toString().substring(0, 6))
                .email(oAuth2UserInfo.getEmail())
                .password("")
                .providerTypeCode(oAuth2UserInfo.getProviderTypeCode())
                .build());
    }
}
