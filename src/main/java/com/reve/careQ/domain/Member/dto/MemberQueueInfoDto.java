package com.reve.careQ.domain.Member.dto;

import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MemberQueueInfoDto {
    private List<RegisterChart> registerCharts;
    private Map<String, Long> waitingCounts;
    private Map<String, RegisterChartStatus> currentStatuses;
    private Map<String, Long> expectedWaitingTimes;

    public static MemberQueueInfoDto of(List<RegisterChart> registerCharts, Map<String, Long> waitingCounts, Map<String, RegisterChartStatus> currentStatuses, Map<String, Long> expectedWaitingTimes) {
        MemberQueueInfoDto dto = new MemberQueueInfoDto();
        dto.registerCharts = registerCharts;
        dto.waitingCounts = waitingCounts;
        dto.currentStatuses = currentStatuses;
        dto.expectedWaitingTimes = expectedWaitingTimes;
        return dto;
    }
}

