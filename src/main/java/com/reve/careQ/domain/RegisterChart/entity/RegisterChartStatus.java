package com.reve.careQ.domain.RegisterChart.entity;

public enum RegisterChartStatus {
    WAITING, //줄서는 중
    ENTER, // 순서가 되었음
    CANCEL, // 줄서기 취소
    COMPLETE // 진료 완료
}
