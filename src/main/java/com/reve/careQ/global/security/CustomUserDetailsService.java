package com.reve.careQ.global.security;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.repository.AdminRepository;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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
        if (isUsernameForAdmin(username)) {
            return loadAdminByUsername(username);
        } else {
            return loadMemberByUsername(username);
        }

    }
    private boolean isUsernameForAdmin(String username) {
        return username.startsWith("admin_");
    }

    private UserDetails loadAdminByUsername(String username) {
        Admin admin = adminRepository.findByUsername(username.substring(6))
                .orElseThrow(() -> new UsernameNotFoundException("Admin username not found: " + username));
        return new User(admin.getUsername(), admin.getPassword(), admin.getGrantedAuthorities());
    }

    private UserDetails loadMemberByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Member username not found: " + username));

        validateProviderType(member);

        return new User(member.getUsername(), member.getPassword(), member.getGrantedAuthorities());
    }

    private void validateProviderType(Member member) {
        if (!"careQ".equals(member.getProviderTypeCode()) && "".equals(member.getPassword())) {
            throw new InternalAuthenticationServiceException(member.getProviderTypeCode(), new Throwable("LinkedAccount"));
        }
    }
}