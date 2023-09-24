package com.reve.careQ.domain.Admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Admin {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long admin_id;

    @CreatedDate
    private LocalDateTime admin_created_at;

    @UpdateTimestamp
    private LocalDateTime admin_updated_at;

    @Column(unique = true)
    @Size(min = 4, max = 16)
    private String admin_username;

    @Column(nullable = false)
    private String admin_password;

    @Column(unique = true)
    private String admin_email;

}