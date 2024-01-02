package com.reve.careQ.domain.RegisterChart.service;

import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartInfoDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.global.rsData.RsData;

import java.util.Optional;

public interface RegisterChartService {
    Optional<RegisterChart> findByAdminIdAndMemberId(Long adminId, Long memberId);
    Optional<RegisterChart> findById(Long id);
    RegisterChartInfoDto getRegisterChartInfo(Long hospitalId, Long subjectId);
    RsData<RegisterChart> insert(Long hospitalId, Long subjectId);
    RsData<String> deleteRegister(Long id);
    RsData<RegisterChart> updateStatus(RegisterChart registerChart, RegisterChartStatus status);
    void processRegisterChart(Long hospitalId, Long subjectId);
}
