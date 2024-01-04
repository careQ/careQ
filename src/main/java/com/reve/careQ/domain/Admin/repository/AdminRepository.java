package com.reve.careQ.domain.Admin.repository;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findById(Long Id);

    Optional<Admin> findByUsername(String username);

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByHospitalIdAndSubjectId(Long hospitalId, Long subjectId);

    List<String> findDistinctHospitalStateBySubjectCode(String subjectCode);

    List<String> findDistinctHospitalCityBySubjectCodeAndHospitalState(String subjectCode, String state);

    List<Hospital> findBySubjectCodeAndHospitalStateContainingAndHospitalCityContainingAndHospitalNameContaining(
            String subjectCode, String state, String city, String name);
}