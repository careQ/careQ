package com.reve.careQ.domain.Member.entity;

import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column(unique = true)
    private String email;

    @Column
    private String providerTypeCode; // 소셜 회원 (KAKAO, NAVER, GOOGLE) , 일반 회원 (careQ)

    public List<? extends GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));

        return grantedAuthorities;

    }

    @OneToMany(mappedBy = "member", fetch = LAZY)
    private List<Chat> chatList;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProviderTypeCode(String providerTypeCode) {
        this.providerTypeCode = providerTypeCode;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}