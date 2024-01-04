package com.reve.careQ.domain.Admin.repository;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findById(Long Id);

    Optional<Admin> findByUsername(String username);

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByHospitalIdAndSubjectId(Long hospitalId, Long subjectId);

    @Query("SELECT DISTINCT admin.hospital.state FROM Admin admin WHERE admin.subject.code = :subjectCode")
    List<String> selectAllStates(@Param("subjectCode") String subjectCode);

    @Query("SELECT DISTINCT admin.hospital.city FROM Admin admin WHERE admin.subject.code = :subjectCode and admin.hospital.state = :state")
    List<String> selectAllCities(@Param("subjectCode") String subjectCode,@Param("state") String state);

    @Query("SELECT admin.hospital FROM Admin admin WHERE admin.subject.code = :subjectCode " +
            "and admin.hospital.state like %:state% and admin.hospital.city like %:city% and admin.hospital.name like %:name%")
    List<Hospital> selectHospitalsByStateAndCity(@Param("subjectCode") String subjectCode, @Param("state") String state, @Param("city") String city, @Param("name") String name);
}