package com.reve.careQ.domain.Admin.entity;

import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Message.entity.Message;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Subject.entity.Subject;
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

    @OneToMany(mappedBy = "admin", fetch = LAZY)
    private List<RegisterChart> registerChartList;

    @OneToMany(mappedBy = "admin", fetch = LAZY)
    private List<Reservation> reservationList;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="hospital_id")
    private Hospital hospital;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="subject_id")
    private Subject subject;

}