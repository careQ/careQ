package com.reve.careQ.domain.RegisterChart.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.RegisterChart.dto.RegisterChartDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.dto.RegisterChartInfoDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.global.rsData.RsData;

import java.util.List;
import java.util.Optional;

public interface RegisterChartService {
    Optional<RegisterChart> findById(Long id);
    RegisterChartInfoDto getRegisterChartInfo(Long hospitalId, Long subjectId);
    RsData<RegisterChart> insert(Long hospitalId, Long subjectId);
    RsData<RegisterChart> updateStatusByAdminAndMember(Admin admin, Long memberId, RegisterChartStatus status);
    void processRegisterChart(Long hospitalId, Long subjectId);
    Optional<RegisterChart> findByAdminIdAndMemberIdAndIsDeletedFalse(Long adminId, Long memberId);
    List<RegisterChartDto> getRegisterChartsByMemberIdAndStatus(Long memberId);
    List<RegisterChartDto> getMedicalCharts();
}

