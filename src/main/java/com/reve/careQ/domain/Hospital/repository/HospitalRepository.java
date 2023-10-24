package com.reve.careQ.domain.Hospital.repository;

import com.reve.careQ.domain.Hospital.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalRepository extends JpaRepository <Hospital, Long> {

    Optional<Hospital> findByCode(String code);

    Optional<Hospital> findById(Long id);
}
