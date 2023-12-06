package com.reve.careQ.domain.Member.repository;

import com.reve.careQ.domain.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long id);

    Optional<Member> findByUsername(String username);

    Optional<Member> findByEmail(String email);

}