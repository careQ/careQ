package com.reve.careQ.domain.Admin.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.global.rsData.RsData;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    Optional<Admin> findById(Long id);
    Optional<Admin> findByUsername(String username);
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByHospitalIdAndSubjectId(Long hospitalId, Long subjectId);
    List<String> selectAllStates(String subjectCode);
    List<String> selectAllCities(String subjectCode, String state);
    List<Hospital> selectHospitalsByStateAndCity(String subjectCode, String state, String city, String name);
    List<Reservation> getReservationsForAdmin(Admin admin);
    List<RegisterChart> getRegisterChartByAdminAndMemberName(Admin admin, String name);
    Optional<Admin> getCurrentAdmin();
    RsData<Admin> join(String hospitalCode, String subjectCode, String username, String password, String email);
    RsData<Admin> findAdmin(String subjectName, String hospitalName);
}