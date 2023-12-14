package com.reve.careQ.domain.Member.controller;

import com.reve.careQ.domain.Member.dto.JoinFormDto;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.global.ApiKeyConfig.ApiKeys;
import com.reve.careQ.global.rq.Rq;
import com.reve.careQ.global.rsData.RsData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final Rq rq;
    private final ApiKeys apiKeys;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false)String error, @RequestParam(value = "exception", required = false)String exception, Model model) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "members/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public String showMembersHome(Model model) {

        RsData<Member> currentUserData = memberService.getCurrentUser();

        if (currentUserData.isSuccess()) {
            Member currentUser = currentUserData.getData();

            List<Reservation> reservations = memberService.getReservationsForMember(currentUser);

            model.addAttribute("reservations", reservations);

            return "members/members-home";
        } else {
            return "redirect:/";
        }

    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "members/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinFormDto joinFormDto) {
        RsData<Member> joinRs = memberService.join("careQ",joinFormDto.getUsername(), joinFormDto.getPassword(), joinFormDto.getEmail());

        if (joinRs.isFail()) {

            return rq.historyBack(joinRs);
        }

        // 아래 링크로 리다이렉트(302, 이동) 하고 그 페이지에서 메세지 보여줌
        return rq.redirectWithMsg("/members/login", joinRs);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/searches")
    public String searches(Model model) {
        model.addAttribute("mapKey", apiKeys.getmapKey());
        model.addAttribute("pharmacyApiKey", apiKeys.getPharmacyApiKey());
        model.addAttribute("hospitalApiKey", apiKeys.getHospitalApiKey());

        return "members/searches";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/passwords")
    public String showFindPassword() {
        return "members/passwords";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/passwords")
    public String findPassword(String username, String email, HttpServletRequest request) {
        RsData<Member> findPasswordRs = memberService.findPassword(username, email);

        if (findPasswordRs.isFail()) {
            return rq.historyBack(findPasswordRs);
        }

        HttpSession session = request.getSession();
        session.setAttribute("findPasswordRs", findPasswordRs);

        return rq.redirectWithMsg("/members/passwords/mail", findPasswordRs);
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/passwords/mail")
    public ModelAndView findPasswordemail(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();

        HttpSession session = request.getSession();
        RsData<Member> findPasswordRs = (RsData<Member>) session.getAttribute("findPasswordRs");

        if (findPasswordRs != null) {
            mv.addObject("email", findPasswordRs.getData().getEmail());
            mv.setViewName("members/passwords-mail");

            memberService.modifyPassword(findPasswordRs.getData().getEmail());
        }

        return mv;
    }

}