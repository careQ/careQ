package com.reve.careQ.domain.Hospital.service;

import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Hospital.repository.HospitalRepository;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalService {
    private final HospitalRepository hospitalRepository;

    @Transactional
    public RsData<Hospital> insert(String code, String name, String state, String city) {

        Hospital hospital = Hospital
                .builder()
                .code(code)
                .name(name)
                .state(state)
                .city(city)
                .build();

        hospitalRepository.save(hospital);

        return RsData.of("S-1", "병원테이블에 삽입되었습니다.", hospital);

    }
}
