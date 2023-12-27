package com.reve.careQ.domain.Admin.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.repository.AdminRepository;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Hospital.service.HospitalService;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.repository.RegisterChartRepository;
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
    private final RegisterChartRepository registerChartRepository;

    public Optional<Admin> findById(Long id) {
        return adminRepository.findById(id);
    }

    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public Optional<Admin> findByHospitalIdAndSubjectId(Long hospitalId, Long subjectId){
        return adminRepository.findByHospitalIdAndSubjectId(hospitalId, subjectId);
    }

    public List<String> selectAllStates(String subjectCode){
        return adminRepository.findDistinctHospitalStateBySubjectCode(subjectCode);
    }

    public List<String> selectAllCities(String subjectCode, String state){
        return adminRepository.findDistinctHospitalCityBySubjectCodeAndHospitalState(subjectCode, state);
    }

    public List<Hospital> selectHospitalsByStateAndCity(String subjectCode, String state, String city, String name){
        return adminRepository.findBySubjectCodeAndHospitalStateContainingAndHospitalCityContainingAndHospitalNameContaining(subjectCode, state, city, name);
    }

    public List<Reservation> getReservationsForAdmin(Admin admin) {
        return reservationRepository.findByAdmin(admin);
    }

    public List<RegisterChart> getRegisterChartByAdminAndMemberName(Admin admin, String name) {
        return registerChartRepository.getRegisterChartByAdminAndMemberName(admin, name);
    }

    @Transactional
    public RsData<Admin> join(String hospitalCode, String subjectCode,String username, String password, String email) {
        Optional<Hospital> hospitalOptional = hospitalService.findByCode(hospitalCode);
        Optional<Subject> subjectOptional = subjectService.findByCode(subjectCode);

        if ((hospitalOptional.isPresent())&&(findByHospitalIdAndSubjectId(hospitalOptional.get().getId(), subjectOptional.get().getId()).isPresent())){
            return RsData.of("F-1", "%s %s 관리자는 이미 사용중입니다.".formatted(hospitalCode, subjectCode));
        }

        if (findByUsername(username).isPresent()) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
        }

        if (subjectOptional.isEmpty()){
            return RsData.of("F-1", "해당 과목코드(%s)는 존재하지 않습니다.".formatted(subjectCode));
        }

        if(findByEmail(email).isPresent()) {
            return RsData.of("F-1", "해당 이메일(%s)은 이미 사용중입니다.".formatted(email));
        }

        if (hospitalOptional.isEmpty()){
            String xmlData = hospitalService.useHospitalApi(hospitalCode).getData();
            String[] parseXml = hospitalService.parseXml(xmlData).getData();

            if (parseXml == null){
                return RsData.of("F-1", "존재하지 않는 병원코드 입니다.");
            }

            hospitalService.insert(parseXml[0], parseXml[1], parseXml[2], parseXml[3]);
            hospitalOptional = hospitalService.findByCode(hospitalCode);
        }

        String encodedPassword = StringUtils.hasText(password) ? passwordEncoder.encode(password) : null;

        Admin admin = Admin
                .builder()
                .hospital(hospitalOptional.get())
                .subject(subjectOptional.get())
                .username(username)
                .password(encodedPassword)
                .email(email)
                .build();

        adminRepository.save(admin);

        return RsData.of("S-1", "회원가입이 완료되었습니다.", admin);

    }

    @Transactional
    public RsData<Admin> findAdmin(String subjectName, String hospitalName){
        Optional<Subject> subjectOptional = subjectService.findByName(subjectName);
        Optional<Hospital> hospitalOptional = hospitalService.findByName(hospitalName);

        if (subjectOptional.isEmpty()){
            return RsData.of("F-1", ("해당 진료과목(%s)은 존재하지 않습니다.\n 진료과목명을 정확하게 입력해주세요.").formatted(subjectName));
        }

        if(hospitalOptional.isEmpty()){
            return RsData.of("F-1", "해당 병원명(%s)은 존재하지 않습니다.\n 병원명을 정확하게 입력해주세요.".formatted(hospitalName));
        }

        Long subjectId = subjectService.findByName(subjectName).get().getId();
        Long hospitalId = hospitalService.findByName(hospitalName).get().getId();

        Optional<Admin> adminOptional = adminRepository.findByHospitalIdAndSubjectId(hospitalId, subjectId);

        return adminOptional.map(admin -> RsData.of("S-1", "해당 관리자가 존재합니다.", admin))
                .orElse(RsData.of("F-1", "해당 관리자가 존재하지 않습니다.\n다른 병원으로 검색해주세요."));
    }

    public RsData<Admin> getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return RsData.of("F-2", "로그인한 사용자 정보를 가져오지 못했습니다.");
        }

        Object principal = authentication.getPrincipal();
        String username = getUsernameFromPrincipal(principal);

        return findByUsername(username)
                .map(admin -> RsData.of("S-1", "현재 로그인한 관리자 정보를 가져왔습니다.", admin))
                .orElse(RsData.of("F-1", "현재 로그인한 관리자 정보를 찾을 수 없습니다."));
    }

    private String getUsernameFromPrincipal(Object principal) {
        return (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : null;
    }
}