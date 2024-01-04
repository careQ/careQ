package com.reve.careQ.domain.Admin.entity;

import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Subject.entity.Subject;
import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Admin extends BaseEntity {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "admin", fetch = LAZY)
    private List<Chat> chatList;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "hospitalId")
    private Hospital hospital;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subjectId")
    private Subject subject;

    public List<? extends GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return grantedAuthorities;

    }
}