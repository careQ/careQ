package com.reve.careQ.domain.Hospital.service;

import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.global.rsData.RsData;

import java.util.Optional;

public interface HospitalService {
    Optional<Hospital> findById(Long id);
    Optional<Hospital> findByCode(String code);
    Optional<Hospital> findByName(String name);
    RsData<Hospital> insert(String code, String name, String state, String city);
    RsData<String> useHospitalApi(String id);
    RsData<String[]> parseXml(String xmlData);
}
