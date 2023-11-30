package com.reve.careQ.domain.RegisterChart.controller;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Hospital.service.HospitalService;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.service.RegisterChartService;
import com.reve.careQ.domain.Subject.entity.Subject;
import com.reve.careQ.domain.Subject.service.SubjectService;
import com.reve.careQ.global.compositePKEntity.CompositePKEntity;
import com.reve.careQ.global.rq.Rq;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/members/subjects/{subject-id}/hospitals/{hospital-id}/queues")
@RequiredArgsConstructor
public class RegisterChartController {

    private final SubjectService subjectService;
    private final HospitalService hospitalService;
    private final AdminService adminService;
    private final MemberService memberService;
    private final RegisterChartService registerChartService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ModelAndView showQueue(@PathVariable("subject-id") Long subjectId, @PathVariable("hospital-id") Long hospitalId, Model model){
        ModelAndView mv = new ModelAndView();
        Subject subject = subjectService.findById(subjectId).get();
        Hospital hospital = hospitalService.findById(hospitalId).get();
        Admin admin = adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId).get();

        Optional<RegisterChart> registerChart = registerChartService.findByIdAdminIdAndIdMemberId(admin.getId(), rq.getMember().getId());

        mv.addObject("register", registerChart);
        mv.addObject("subject",subject);
        mv.addObject("hospital",hospital);
        mv.setViewName("members/queues");

        return mv;
    }

    // 줄서기 버튼
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public String createRegister(@PathVariable("subject-id") Long subjectId,
                                    @PathVariable("hospital-id") Long hospitalId) {

        RsData<RegisterChart> registerChartRs = registerChartService.insert(hospitalId, subjectId);

        if (registerChartRs.isSuccess()) {
            // 줄서기 생성 성공
            return "redirect:/members/subjects/" + subjectId + "/hospitals/" + hospitalId + "/queues";
        } else {
            // 줄서기 생성 실패
            return rq.historyBack(registerChartRs);
        }
    }

    //줄서기 삭제
    @DeleteMapping
    public String deleteRegister(@PathVariable("subject-id") Long subjectId,
                                    @PathVariable("hospital-id") Long hospitalId) {

        Optional<Admin> adminOptional = adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId);

        if (adminOptional.isEmpty()) {
            return "redirect:/members";
        } else {
            Admin admin = adminOptional.get();

            RsData<Member> currentUserData = memberService.getCurrentUser();

            if (currentUserData.isSuccess()) {
                Member currentUser = currentUserData.getData();

                CompositePKEntity id = new CompositePKEntity();
                id.setAdminId(admin.getId());
                id.setMemberId(currentUser.getId());

                // 줄서기 정보 삭제
                RsData<String> deleteResult = registerChartService.deleteRegister(id);

                if (deleteResult.isSuccess()) {
                    return "redirect:/members";
                } else {
                    return rq.historyBack(deleteResult);
                }
            } else {
                return rq.historyBack(currentUserData);
            }
        }
    }
}
