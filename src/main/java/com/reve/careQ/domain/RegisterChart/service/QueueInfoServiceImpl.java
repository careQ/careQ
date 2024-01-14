package com.reve.careQ.domain.RegisterChart.service;

import com.reve.careQ.domain.RegisterChart.dto.QueueInfoDto;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.repository.MemberRepository;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.RegisterChart.repository.RegisterChartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueueInfoServiceImpl implements QueueInfoService {
    private final RegisterChartRepository registerChartRepository;
    private final MemberRepository memberRepository;


    @Override
    public QueueInfoDto getQueueInfoData(Long memberId) {
        Member member = findMemberById(memberId);
        List<RegisterChart> registerCharts = getRegisterChartsForMember(member);
        return createQueueInfoDto(registerCharts, member);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 ID를 찾을 수 없습니다:" + memberId));
    }

    private List<RegisterChart> getRegisterChartsForMember(Member member) {
        return registerChartRepository.findByMemberAndIsDeletedFalse(member);
    }

    private QueueInfoDto createQueueInfoDto(List<RegisterChart> registerCharts, Member member) {
        if (registerCharts.isEmpty()) {
            return handleNoWaitings(registerCharts);
        } else {
            return handleWaitingsExist(registerCharts, member);
        }
    }

    private QueueInfoDto handleNoWaitings(List<RegisterChart> registerCharts) {
        Map<String, Long> waitingCounts = new HashMap<>();
        Map<String, RegisterChartStatus> currentStatuses = new HashMap<>();
        Map<String, Long> expectedWaitingTimes = new HashMap<>();

        waitingCounts.put("줄서기 내역이 없습니다.", null);
        currentStatuses.put("줄서기 내역이 없습니다.", null);
        expectedWaitingTimes.put("줄서기 내역이 없습니다.", null);

        return QueueInfoDto.of(registerCharts, waitingCounts, currentStatuses, expectedWaitingTimes);
    }

    private QueueInfoDto handleWaitingsExist(List<RegisterChart> registerCharts, Member member) {
        Function<RegisterChart, String> keyMapper = this::getHospitalAndSubject;

        Map<String, Long> waitingCounts = createWaitingCountsMap(registerCharts, member, keyMapper);
        Map<String, RegisterChartStatus> currentStatuses = createCurrentStatusesMap(registerCharts, keyMapper);
        Map<String, Long> expectedWaitingTimes = createExpectedWaitingTimesMap(registerCharts, waitingCounts, keyMapper);

        return QueueInfoDto.of(registerCharts, waitingCounts, currentStatuses, expectedWaitingTimes);
    }

    private Map<String, Long> createWaitingCountsMap(List<RegisterChart> registerCharts, Member member, Function<RegisterChart, String> keyMapper) {
        return registerCharts.stream().collect(
                Collectors.toMap(
                        keyMapper,
                        chart -> getWaitingCount(member, chart),
                        (oldValue, newValue) -> newValue
                )
        );
    }

    private Map<String, RegisterChartStatus> createCurrentStatusesMap(List<RegisterChart> registerCharts, Function<RegisterChart, String> keyMapper) {
        return registerCharts.stream().collect(
                Collectors.toMap(
                        keyMapper,
                        RegisterChart::getStatus,
                        (oldValue, newValue) -> newValue
                )
        );
    }

    private Map<String, Long> createExpectedWaitingTimesMap(List<RegisterChart> registerCharts, Map<String, Long> waitingCounts, Function<RegisterChart, String> keyMapper) {
        return registerCharts.stream().collect(
                Collectors.toMap(
                        keyMapper,
                        chart -> calculateExpectedWaitingTime(waitingCounts.get(getHospitalAndSubject(chart)), chart.getStatus()),
                        (oldValue, newValue) -> newValue
                )
        );
    }

    private String getHospitalAndSubject(RegisterChart registerChart) {
        return registerChart.getAdmin().getHospital().getName() + "/" + registerChart.getAdmin().getSubject().getName();
    }

    private Long getWaitingCount(Member member, RegisterChart registerChart) {
        List<RegisterChart> allRegisterCharts = registerChartRepository.findByAdminHospitalAndAdminSubjectAndIsDeletedFalse(registerChart.getAdmin().getHospital(), registerChart.getAdmin().getSubject());
        List<RegisterChart> memberRegisterCharts = registerChartRepository.findByMemberAndAdminHospitalAndAdminSubjectAndIsDeletedFalse(member, registerChart.getAdmin().getHospital(), registerChart.getAdmin().getSubject());

        if (memberRegisterCharts.isEmpty()) {
            return (long) allRegisterCharts.size();
        } else {
            LocalDateTime memberTime = memberRegisterCharts.get(0).getTime();
            return allRegisterCharts.stream().filter(chart -> chart.getTime().isBefore(memberTime)).count();
        }
    }

    private Long calculateExpectedWaitingTime(Long waitingCount, RegisterChartStatus currentStatus) {
        if (waitingCount != null && currentStatus != RegisterChartStatus.CANCEL && currentStatus != RegisterChartStatus.COMPLETE) {
            return waitingCount * 5;
        } else {
            return null;
        }
    }
}