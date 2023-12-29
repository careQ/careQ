package com.reve.careQ.domain.Member.controller;

import com.reve.careQ.domain.Member.dto.JoinFormDto;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.global.ApiKeyConfig.ApiKeys;
import com.reve.careQ.global.rq.Rq;
import com.reve.careQ.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public String showLogin() {
        return "members/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public String showMembersHome(Model model) {

        Optional<Member> currentUserOptional = memberService.getCurrentUser();

        if (currentUserOptional.isPresent()) {
            Member currentUser = currentUserOptional.get();

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

}