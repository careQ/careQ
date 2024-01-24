package com.reve.careQ.domain.Pharmacy.controller;

import com.reve.careQ.domain.Pharmacy.dto.PharmacyDto;
import com.reve.careQ.domain.Pharmacy.service.PharmacyService;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members/searches/pharmacies")
@RequiredArgsConstructor
public class PharmacyController {

    private final PharmacyService pharmacyService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public RsData<Page<PharmacyDto>> getAllPharmacies(Pageable pageable) {
        Page<PharmacyDto> pharmacies = pharmacyService.getAllPharmacies(pageable);
        return RsData.of("S-1", "약국 정보 조회 성공", pharmacies);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public RsData<String> savePharmaciesFromApi() {
        pharmacyService.updatePharmacies();
        return RsData.of("S-1","약국 정보가 성공적으로 저장되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/states")
    public RsData<List<String>> getStates() {
        List<String> states = pharmacyService.getAllStates();
        return RsData.of("S-1", "시도 정보 조회 성공", states);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/states/{state}")
    public RsData<List<String>> getDistinctCitiesByState(@PathVariable String state) {
        List<String> cities = pharmacyService.getCitiesByState(state);
        return RsData.of("S-1", "구군 정보 조회 성공", cities);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/states/{state}/{city}")
    public RsData<List<PharmacyDto>> getParksByStateAndCity(@PathVariable String state, @PathVariable String city) {
        List<PharmacyDto> pharmacies = pharmacyService.getPharmacyByStateAndCity(state, city);
        return RsData.of("S-1", "시도/구군에 해당하는 정보 조회 성공", pharmacies);
    }
}