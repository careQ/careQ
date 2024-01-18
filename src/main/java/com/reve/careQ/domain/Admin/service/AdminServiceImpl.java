package com.reve.careQ.domain.Admin.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.repository.AdminRepository;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Hospital.service.HospitalService;
import com.reve.careQ.domain.RegisterChart.dto.RegisterChartDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

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
        return adminRepository.selectAllStates(subjectCode);
    }

    public List<String> selectAllCities(String subjectCode, String state){
        return adminRepository.selectAllCities(subjectCode, state);
    }

    public List<Hospital> selectHospitalsByStateAndCity(String subjectCode, String state, String city, String name){
        return adminRepository.selectHospitalsByStateAndCity(subjectCode, state, city, name);
    }

    public List<Reservation> getReservationsForAdmin(Admin admin) {
        return reservationRepository.findByAdmin(admin);
    }

    public List<RegisterChart> getRegisterChartByAdminAndMemberName(Admin admin, String name) {
        return registerChartRepository.getRegisterChartByAdminAndMemberName(admin, name);
    }

    @Override
    public List<RegisterChartDto> getRegisterChartDtoByMemberName(String name) {
        Admin currentAdmin = getCurrentAdmin().orElseThrow(() -> new RuntimeException("인증되지 않은 관리자입니다."));
        List<RegisterChart> registerCharts = getRegisterChartByAdminAndMemberName(currentAdmin, name);
        return registerCharts.stream().map(RegisterChart::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RsData<Admin> join(String hospitalCode, String subjectCode,String username, String password, String email) {
        Optional<Hospital> hospitalOptional = hospitalService.findByCode(hospitalCode);
        Optional<Subject> subjectOptional = subjectService.findByCode(subjectCode);

        RsData<Admin> validationData = validateJoinRequest(hospitalOptional, subjectOptional, hospitalCode, subjectCode, username, email);

        if (validationData.isFail()) {
            return validationData;
        }

        Hospital hospital = hospitalService.findByCode(hospitalCode).get();
        String encodedPassword = encodePassword(password);
        Admin admin = createAdmin(hospital, subjectOptional.get(), username, encodedPassword, email);

        return RsData.of("S-1", "회원가입이 완료되었습니다.", admin);
    }

    @Override
    @Transactional
    public RsData<Admin> findAdmin(String subjectName, String hospitalName){
        Optional<Subject> subjectOptional = subjectService.findByName(subjectName);
        Optional<Hospital> hospitalOptional = hospitalService.findByName(hospitalName);

        RsData<Admin> validationData = validateFindAdminRequest(subjectOptional, hospitalOptional, subjectName, hospitalName);

        if (validationData.isFail()) {
            return validationData;
        }

        Long subjectId = subjectService.findByName(subjectName).get().getId();
        Long hospitalId = hospitalService.findByName(hospitalName).get().getId();

        Optional<Admin> adminOptional = adminRepository.findByHospitalIdAndSubjectId(hospitalId, subjectId);

        return adminOptional.map(admin -> RsData.of("S-1", "해당 관리자가 존재합니다.", admin))
                .orElse(RsData.of("F-1", "해당 관리자가 존재하지 않습니다.\n다른 병원으로 검색해주세요."));
    }

    @Override
    @Transactional
    public Optional<Admin> getCurrentAdmin() {
        Authentication authentication = getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("인증되지 않은 관리자입니다.");
        }

        String username = authentication.getName();
        return findByUsername(username);
    }

    private Admin createAdmin(Hospital hospital, Subject subject, String username, String password, String email){
        Admin admin = Admin.builder()
                .hospital(hospital)
                .subject(subject)
                .username(username)
                .password(password)
                .email(email)
                .build();

        return adminRepository.save(admin);
    }

    private String encodePassword(String password){
        return StringUtils.hasText(password) ? passwordEncoder.encode(password) : null;
    }

    private RsData<Admin> validateJoinRequest(Optional<Hospital> hospitalOptional, Optional<Subject> subjectOptional, String hospitalCode, String subjectCode,String username, String email) {

        RsData<Admin> adminValidation = isAdminAlreadyExistRs(hospitalOptional, subjectOptional);
        if (!adminValidation.isSuccess()) {
            return adminValidation;
        }

        RsData<Admin> usernameValidation = isUsernameAlreadyUsedRs(username);
        if (!usernameValidation.isSuccess()) {
            return usernameValidation;
        }

        RsData<Admin> emailValidation = isEmailAlreadyUsedRs(email);
        if (!emailValidation.isSuccess()) {
            return emailValidation;
        }

        RsData<Admin> subjectValidation = isSubjectAlreadyExistRs(subjectCode);
        if (!subjectValidation.isSuccess()) {
            return subjectValidation;
        }

        RsData<Admin> hospitalIfNotExistRs = createHospitalIfNotExists(hospitalCode, hospitalOptional);
        if (hospitalIfNotExistRs.isFail()) {
            return hospitalIfNotExistRs;
        }

        return RsData.success();
    }

    private RsData<Admin> isUsernameAlreadyUsedRs (String username) {
        return findByUsername(username).map(admin -> RsData.<Admin>of("F-2", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username)))
                .orElse(RsData.success());
    }

    private RsData<Admin> isEmailAlreadyUsedRs (String email) {
        return findByEmail(email).map(admin -> RsData.<Admin>of("F-3", "해당 이메일(%s)은 이미 사용중입니다.".formatted(email)))
                .orElse(RsData.success());
    }

    private boolean isSubjectAlreadyExist(String subjectCode){
        return subjectService.findByCode(subjectCode).isPresent();
    }

    private RsData<Admin> isSubjectAlreadyExistRs(String subjectCode){
        return isSubjectAlreadyExist(subjectCode) ? RsData.success() : RsData.of("F-4", "해당 과목코드(%s)는 존재하지 않습니다.".formatted(subjectCode));
    }

    private boolean isAdminAlreadyExist(Optional<Hospital> hospitalOptional, Optional<Subject> subjectOptional) {
        return hospitalOptional.isPresent() &&
                subjectOptional.isPresent() &&
                findByHospitalIdAndSubjectId(hospitalOptional.get().getId(), subjectOptional.get().getId()).isPresent();
    }

    private RsData<Admin> isAdminAlreadyExistRs(Optional<Hospital> hospitalOptional, Optional<Subject> subjectOptional){
        return isAdminAlreadyExist(hospitalOptional, subjectOptional) ? RsData.of("F-1", "%s %s 관리자는 이미 사용중입니다.".formatted(hospitalOptional.get().getCode(), subjectOptional.get().getCode())) : RsData.success();
    }

    private RsData<Admin> createHospitalIfNotExists(String hospitalCode, Optional<Hospital> hospitalOptional) {
        if (hospitalOptional.isEmpty()) {
            String xmlData = hospitalService.useHospitalApi(hospitalCode).getData();
            String[] parseXml = hospitalService.parseXml(xmlData).getData();

            if (parseXml == null) {
                return RsData.of("F-5", "존재하지 않는 병원코드 입니다.");
            }

            hospitalService.insert(parseXml[0], parseXml[1], parseXml[2], parseXml[3]);
        }
        return RsData.success();
    }

    private RsData<Admin> validateFindAdminRequest(Optional<Subject> subjectOptional, Optional<Hospital> hospitalOptional,
                                                   String subjectName, String hospitalName) {
        RsData<Admin> subjectNameValidation = isSubjectAlreadyExistByNameRs(subjectOptional, subjectName);
        if (!subjectNameValidation.isSuccess()) {
            return subjectNameValidation;
        }

        RsData<Admin> hospitalNameValidation = isHospitalAlreadyExistByNameRs(hospitalOptional, hospitalName);
        if (!hospitalNameValidation.isSuccess()) {
            return hospitalNameValidation;
        }

        return RsData.success();
    }

    private RsData<Admin> isSubjectAlreadyExistByNameRs(Optional<Subject> subjectOptional, String subjectName) {
        return subjectOptional.map(subject -> RsData.<Admin>success())
                .orElse(RsData.of("F-2", "해당 진료과목(%s)은 존재하지 않습니다.\n진료과목명을 정확하게 입력해주세요.".formatted(subjectName)));
    }

    private RsData<Admin> isHospitalAlreadyExistByNameRs(Optional<Hospital> hospitalOptional, String hospitalName) {
        return hospitalOptional.map(subject -> RsData.<Admin>success())
                .orElse(RsData.of("F-3", "해당 병원명(%s)은 존재하지 않습니다.\n 병원명을 정확하게 입력해주세요.".formatted(hospitalName)));
    }

    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    @Transactional
    public List<Reservation> getReservationsForCurrentAdmin() {
        Admin currentAdmin = getCurrentAdmin()
                .orElseThrow(() -> new RuntimeException("인증되지 않은 관리자입니다."));

        return getReservationsForAdmin(currentAdmin);
    }
}