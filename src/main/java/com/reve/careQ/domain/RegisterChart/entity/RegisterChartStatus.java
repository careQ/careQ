package com.reve.careQ.domain.RegisterChart.entity;

public enum RegisterChartStatus {
    WAITING, //줄서는 중
    ENTER, // 순서가 되었음(진료 중, 대기 중)
    CANCEL, // 줄서기 취소
    COMPLETE, // 진료 완료
    CANCELLED // 환자 예약 취소
}
