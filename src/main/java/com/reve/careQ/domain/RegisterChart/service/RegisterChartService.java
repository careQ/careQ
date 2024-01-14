package com.reve.careQ.domain.RegisterChart.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Member.dto.OnsiteRegisterDto;
import com.reve.careQ.domain.RegisterChart.dto.RegisterQueueInfoDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.global.rsData.RsData;

import java.util.Optional;

public interface RegisterChartService {
    Optional<RegisterChart> findById(Long id);
    RegisterQueueInfoDto getRegisterQueueInfo(Long hospitalId, Long subjectId);
    RsData<RegisterChart> insert(Long hospitalId, Long subjectId);
    RsData<RegisterChart> updateStatusByAdminAndMember(Admin admin, Long memberId, RegisterChartStatus status);
    void processRegisterChart(Long hospitalId, Long subjectId);
    RegisterChart registerNewMember(String providerType, String username, String tempPassword, String email);
    void registerNewUser(OnsiteRegisterDto onsiteRegisterDto);
}

