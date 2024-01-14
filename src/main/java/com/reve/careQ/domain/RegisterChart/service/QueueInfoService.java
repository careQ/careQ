package com.reve.careQ.domain.RegisterChart.service;

import com.reve.careQ.domain.RegisterChart.dto.QueueInfoDto;

public interface QueueInfoService {
    QueueInfoDto getQueueInfoData(Long memberId);
}