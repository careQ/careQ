package com.reve.careQ.domain.Admin.entity;

import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.domain.HospSub.entity.HospSub;
import com.reve.careQ.domain.Message.entity.Message;
import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Admin extends BaseEntity {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Column(unique = true)
    @Size(min = 4, max = 16)
    private String admin_username;

    @Column(nullable = false)
    private String admin_password;

    @Column(unique = true)
    private String admin_email;

    @OneToMany(mappedBy = "admin", fetch = LAZY)
    private List<Chat> chatList;

    @OneToMany(mappedBy = "admin", fetch = LAZY)
    private List<Message> messageList;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="hospsub_id")
    private HospSub hospSub;

}