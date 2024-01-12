package com.reve.careQ.domain.RegisterChart.repository;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegisterChartRepository extends JpaRepository<RegisterChart, Long> {
    Optional<RegisterChart> findByAdminIdAndMemberIdAndIsDeletedFalse(Long adminId, Long memberId);
    Optional<RegisterChart> findRegisterChartByAdminIdAndMemberIdAndIsDeletedFalse(Long adminId, Long memberId);
    boolean existsByAdminIdAndMemberIdAndIsDeletedFalse(Long adminId, Long memberId);
    Optional<RegisterChart> findByAdminIdAndMemberId(Long adminId, Long memberId);
    @Query("SELECT registerchart FROM RegisterChart registerchart WHERE registerchart.admin = :admin and registerchart.isDeleted = false " +
            "and registerchart.member.username like %:name% order by registerchart.time")
    List<RegisterChart> getRegisterChartByAdminAndMemberName(@Param("admin") Admin admin, @Param("name") String name);
}
