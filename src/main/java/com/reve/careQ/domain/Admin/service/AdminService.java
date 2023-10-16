package com.reve.careQ.domain.Admin.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.repository.AdminRepository;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Hospital.repository.HospitalRepository;
import com.reve.careQ.domain.Subject.entity.Subject;
import com.reve.careQ.domain.Subject.repository.SubjectRepository;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final PasswordEncoder passwordEncoder;

    private final AdminRepository adminRepository;

    private final HospitalRepository hospitalRepository;

    private final SubjectRepository subjectRepository;

    private Optional<Admin> findByHospitalCodeAndSubjectCode(String hospitalCode, String subjectCode){
        return adminRepository.findByHospitalCodeAndSubjectCode(hospitalCode, subjectCode);
    }

    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    @Transactional
    public RsData<Admin> join(String hospitalCode, String subjectCode,String username, String password) {
        if (findByHospitalCodeAndSubjectCode(hospitalCode, subjectCode).isPresent()) {
            return RsData.of("F-1", "%s %s 관리자는 이미 사용중입니다.".formatted(hospitalCode, subjectCode));
        }

        if (findByUsername(username).isPresent()) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
        }

        if (subjectRepository.findByCode(subjectCode).isEmpty()){
            return RsData.of("F-1", "해당 과목코드(%s)는 존재하지 않습니다.".formatted(subjectCode));
        }

        if (StringUtils.hasText(password)) {
            password = passwordEncoder.encode(password);
        }

        Hospital hospital = hospitalRepository.findByCode(hospitalCode).get();
        Subject subject = subjectRepository.findByCode(subjectCode).get();

        Admin admin = Admin
                .builder()
                .hospital(hospital)
                .subject(subject)
                .username(username)
                .password(password)
                .build();

        adminRepository.save(admin);

        return RsData.of("S-1", "회원가입이 완료되었습니다.", admin);

    }

}