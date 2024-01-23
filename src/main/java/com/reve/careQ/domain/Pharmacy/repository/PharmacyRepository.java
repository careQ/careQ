package com.reve.careQ.domain.Pharmacy.repository;

import com.reve.careQ.domain.Pharmacy.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
    @Query("SELECT DISTINCT p.state FROM Pharmacy p")
    List<String> findAllStates();

    @Query("SELECT DISTINCT p.city FROM Pharmacy p WHERE p.state = :state")
    List<String> findCitiesByState(@Param("state") String state);

    List<Pharmacy> findByStateAndCity(String state, String city);
}
