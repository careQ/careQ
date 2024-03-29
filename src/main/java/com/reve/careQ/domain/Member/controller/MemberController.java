package com.reve.careQ.domain.Member.controller;

import com.reve.careQ.domain.Member.dto.JoinFormDto;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.RegisterChart.dto.QueueInfoDto;
import com.reve.careQ.domain.RegisterChart.service.RegisterChartService;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Reservation.service.ReservationService;
import com.reve.careQ.global.ApiKeyConfig.ApiKeys;
import com.reve.careQ.global.rq.Rq;
import com.reve.careQ.global.rsData.RsData;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final RegisterChartService registerChartService;
    private final ReservationService reservationService;
    private final Rq rq;
    private final ApiKeys apiKeys;

    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false)String error, @RequestParam(value = "exception", required = false)String exception, Model model) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "members/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public String showMembersHome(Model model) {
        Optional<Member> currentUserOptional = memberService.getCurrentUser();

        currentUserOptional.ifPresent(currentUser -> {
            List<QueueInfoDto> queueInfo = registerChartService.getQueueInfoByMemberId(currentUser.getId());
            List<Reservation> reservations = reservationService.findByMemberIdAndIsDeletedFalse(currentUser.getId());

            model.addAttribute("queueInfoDtoList", queueInfo);
            model.addAttribute("reservations", reservations);
        });

        return "members/members-home";
    }

    @GetMapping("/join")
    public String showJoin() {
        return "members/join";
    }

    @PostMapping("/join")
    public String join(@Valid JoinFormDto joinFormDto) {
        RsData<Member> joinRs = memberService.join("careQ",joinFormDto.getUsername(), joinFormDto.getPassword(), joinFormDto.getEmail());

        return redirectToPageWithMsg("/members/login", joinRs);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/searches")
    public String searches(Model model) {
        addApiKeysToModel(model);

        return "members/searches";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage")
    public ModelAndView mypage(boolean checkPassword) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("checkPassword", checkPassword);
        mv.setViewName("members/mypage");
        return mv;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/mypage", params = {"password"})
    @ResponseBody
    public boolean checkPassword(@RequestParam(name="password",required=false,defaultValue="") String password) {
        return memberService.checkPassword(password, rq.getMember().getId());
    }

    @GetMapping("/passwords")
    public String showFindPassword() {
        return "members/passwords";
    }

    @PostMapping("/passwords")
    public String findPassword(String username, String email, HttpSession session) {
        RsData<Member> findPasswordRs = memberService.findPassword(username, email);

        session.setAttribute("findPasswordRs", findPasswordRs);

        return redirectToPageWithMsg("/members/passwords/mail", findPasswordRs);
    }

    @GetMapping("/passwords/mail")
    public ModelAndView findPasswordEmail(HttpSession session) {
        ModelAndView mv = new ModelAndView();

        RsData<Member> findPasswordRs = (RsData<Member>) session.getAttribute("findPasswordRs");

        if (findPasswordRs != null) {
            String email = findPasswordRs.getData().getEmail();
            mv.addObject("email", email);
            mv.setViewName("members/passwords-mail");

            modifyPasswordAndSendEmail(email);
        }
        return mv;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/mypage", params = {"username", "password"})
    @ResponseBody
    public boolean changeUsername(@RequestParam(name="username",required=false,defaultValue="") String username,
                                  @RequestParam(name="password",required=false,defaultValue="") String password) {
        RsData<Member> memberRs = memberService.changeUsername(rq.getMember(), username);
        if(memberRs.isSuccess()){
            rq.refresh(memberRs.getData(), password);
            return true;
        }

        return false;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/mypage", params = {"newpassword"})
    @ResponseBody
    public boolean changePassword(@RequestParam(name="newpassword",required=false,defaultValue="") String newpassword) {
        RsData<Member> memberRs = memberService.changePassword(rq.getMember(), newpassword);
        if(memberRs.isSuccess()){
            rq.refresh(memberRs.getData(), newpassword);
            return true;
        }

        return false;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage/medicalrecords")
    public ModelAndView medicalrecords() {
        ModelAndView mv = new ModelAndView();

        mv.addObject("records", registerChartService.getMedicalCharts());
        mv.setViewName("members/medical-records");

        return mv;
    }

    private String redirectToPageWithMsg(String page, RsData<Member> rsData) {
        return rsData.isFail() ? rq.historyBack(rsData) : rq.redirectWithMsg(page, rsData);
    }

    private void modifyPasswordAndSendEmail(String email){
        memberService.modifyPassword(email);
    }

    private void addApiKeysToModel(Model model) {
        model.addAllAttributes(Map.of(
                "mapKey", apiKeys.getmapKey(),
                "pharmacyApiKey", apiKeys.getPharmacyApiKey(),
                "hospitalApiKey", apiKeys.getHospitalApiKey()
        ));
    }
}