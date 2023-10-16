package com.reve.careQ.domain.Admin.repository;

import com.reve.careQ.domain.Admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByHospitalCodeAndSubjectCode(String hospitalCode, String subjectCode);

    Optional<Admin> findByUsername(String username);

}