package com.reve.careQ.domain.Pharmacy.service;

import com.reve.careQ.domain.Pharmacy.dto.PharmacyDto;
import com.reve.careQ.domain.Pharmacy.entity.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PharmacyService {
    Optional<Pharmacy> findById(Long id);
    Page<PharmacyDto> getAllPharmacies(Pageable pageable);
    List<String> getAllStates();
    List<String> getCitiesByState(String state);
    List<PharmacyDto> getPharmacyByStateAndCity(String state, String city);
    void updatePharmacies();
}
