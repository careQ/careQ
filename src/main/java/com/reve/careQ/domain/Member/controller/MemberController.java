package com.reve.careQ.domain.Member.controller;

import com.reve.careQ.domain.Member.dto.JoinFormDto;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.Reservation.entity.Reservation;
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

        Optional<Member> currentUserOptional = memberService.getCurrentUser();

        currentUserOptional.ifPresent(currentUser -> {
            List<Reservation> reservations = memberService.getReservationsForMember(currentUser);
            model.addAttribute("reservations", reservations);
        });

        return "members/members-home";
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
    public String mypage() {
        return "members/mypage";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/passwords")
    public String showFindPassword() {
        return "members/passwords";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/passwords")
    public String findPassword(String username, String email, HttpSession session) {
        RsData<Member> findPasswordRs = memberService.findPassword(username, email);

        session.setAttribute("findPasswordRs", findPasswordRs);

        return redirectToPageWithMsg("/members/passwords/mail", findPasswordRs);
    }

    @PreAuthorize("isAnonymous()")
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