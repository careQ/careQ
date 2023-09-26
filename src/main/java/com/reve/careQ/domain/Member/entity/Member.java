package com.reve.careQ.domain.Member.entity;

import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.domain.Message.entity.Message;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.Reservation.entity.Reservation;
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
public class Member extends BaseEntity {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Column(unique = true)
    @Size(min = 4, max = 16)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "member", fetch = LAZY)
    private List<RegisterChart> registerCharts;

    @OneToMany(mappedBy = "member", fetch = LAZY)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "member", fetch = LAZY)
    private List<Chat> chatList;

    @OneToMany(mappedBy = "member", fetch = LAZY)
    private List<Message> messageList;

}