package com.reve.careQ.domain.Admin.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.repository.AdminRepository;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Hospital.service.HospitalService;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Reservation.repository.ReservationRepository;
import com.reve.careQ.domain.Subject.entity.Subject;
import com.reve.careQ.domain.Subject.service.SubjectService;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final PasswordEncoder passwordEncoder;

    private final AdminRepository adminRepository;

    private final HospitalService hospitalService;

    private final SubjectService subjectService;
    private final ReservationRepository reservationRepository;

    public Optional<Admin> findById(Long id) {
        return adminRepository.findById(id);
    }

    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public Optional<Admin> findByHospitalIdAndSubjectId(Long hospitalId, Long subjectId){
        return adminRepository.findByHospitalIdAndSubjectId(hospitalId, subjectId);
    }

    public List<String> selectAllStates(String subjectCode){
        return adminRepository.selectAllStates(subjectCode);
    }

    public List<String> selectAllCities(String subjectCode, String state){
        return adminRepository.selectAllCities(subjectCode, state);
    }

    public List<Hospital> selectHospitalsByStateAndCity(String subjectCode, String state, String city, String name){
        return adminRepository.selectHospitalsByStateAndCity(subjectCode, state, city, name);
    }

    @Transactional
    public RsData<Admin> join(String hospitalCode, String subjectCode,String username, String password) {
        Optional<Hospital> hospital = hospitalService.findByCode(hospitalCode);
        Optional<Subject> subject = subjectService.findByCode(subjectCode);

        if ((hospital.isPresent())&&(findByHospitalIdAndSubjectId(hospital.get().getId(), subject.get().getId()).isPresent())){
            return RsData.of("F-1", "%s %s 관리자는 이미 사용중입니다.".formatted(hospitalCode, subjectCode));
        }

        if (findByUsername(username).isPresent()) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
        }

        if (subjectService.findByCode(subjectCode).isEmpty()){
            return RsData.of("F-1", "해당 과목코드(%s)는 존재하지 않습니다.".formatted(subjectCode));
        }

        if (hospitalService.findByCode(hospitalCode).isEmpty()){
            String xmlData = hospitalService.useHospitalApi(hospitalCode).getData();
            String[] parseXml = hospitalService.parseXml(xmlData).getData();

            if (parseXml == null){
                return RsData.of("F-1", "존재하지 않는 병원코드 입니다.");
            }


            hospitalService.insert(parseXml[0], parseXml[1], parseXml[2], parseXml[3]);
        }

        if (StringUtils.hasText(password)) {
            password = passwordEncoder.encode(password);
        }

        Hospital inputhospital = hospitalService.findByCode(hospitalCode).get();
        Subject inputsubject = subjectService.findByCode(subjectCode).get();

        Admin admin = Admin
                .builder()
                .hospital(inputhospital)
                .subject(inputsubject)
                .username(username)
                .password(password)
                .build();

        adminRepository.save(admin);

        return RsData.of("S-1", "회원가입이 완료되었습니다.", admin);

    }

    @Transactional
    public RsData<Admin> findAdmin(String subjectName, String hospitalName){
        if (subjectService.findByName(subjectName).isEmpty()){
            return RsData.of("F-1", ("해당 진료과목(%s)은 존재하지 않습니다.\n 진료과목명을 정확하게 입력해주세요.").formatted(subjectName));
        }

        if(hospitalService.findByName(hospitalName).isEmpty()){
            return RsData.of("F-1", "해당 병원명(%s)은 존재하지 않습니다.\n 병원명을 정확하게 입력해주세요.".formatted(hospitalName));
        }

        Long subjectId = subjectService.findByName(subjectName).get().getId();
        Long hospitalId = hospitalService.findByName(hospitalName).get().getId();

        Optional<Admin> admin = adminRepository.findByHospitalIdAndSubjectId(hospitalId, subjectId);

        if(admin.isEmpty()){
            return RsData.of("F-1", ("해당 관리자(%s %s)가 존재하지 않습니다.\n 다른 병원으로 검색해주세요.").formatted(hospitalName, subjectName));
        }

        return RsData.of("S-1", "해당 관리자가 존재합니다.", admin.get());
    }

    public RsData<Admin> getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();

                // username을 이용해 현재 로그인한 관리자 찾기
                Optional<Admin> currentAdmin = findByUsername(username);

                if (currentAdmin.isPresent()) {
                    return RsData.of("S-1", "현재 로그인한 관리자 정보를 가져왔습니다.", currentAdmin.get());
                } else {
                    return RsData.of("F-1", "현재 로그인한 관리자 정보를 찾을 수 없습니다.");
                }
            }
        }

        return RsData.of("F-2", "로그인한 사용자 정보를 가져오지 못했습니다.");
    }

    public List<Reservation> getReservationsForAdmin(Admin admin) {
        return reservationRepository.findByAdmin(admin);
    }

}