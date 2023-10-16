package com.reve.careQ.global.security;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.repository.AdminRepository;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    private final AdminRepository adminRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if(username.substring(0,6).equals("admin_")){
            Admin admin = adminRepository.findByUsername(username.substring(6))
                    .orElseThrow(() -> new UsernameNotFoundException("username(%s) not found".formatted(username.substring(6))));
            return new User(admin.getUsername(), admin.getPassword(), admin.getGrantedAuthorities());
        }

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username(%s) not found".formatted(username)));

        return new User(member.getUsername(), member.getPassword(), member.getGrantedAuthorities());

    }
}