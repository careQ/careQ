package com.reve.careQ.domain.Admin.controller;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Admin.dto.JoinFormDto;
import com.reve.careQ.global.rq.Rq;
import com.reve.careQ.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final Rq rq;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin() {
        return "admins/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public String showAdminsHome() {
        return "admins/admins-home";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "admins/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinFormDto joinFormDto) {
        RsData<Admin> joinRs = adminService.join(joinFormDto.getHospitalCode(), joinFormDto.getSubjectCode(), joinFormDto.getUsername(), joinFormDto.getPassword());

        if (joinRs.isFail()) {

            return rq.historyBack(joinRs);
        }

        // 아래 링크로 리다이렉트(302, 이동) 하고 그 페이지에서 메세지 보여줌
        return rq.redirectWithMsg("/admins/login", joinRs);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/chatrooms")
    public String showChatrooms() {
        return "admins/chatrooms";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/chatrooms/{chatroom-id}")
    public String showChat(@PathVariable("chatroom-id") Long id) {
        return "admins/chat";
    }

}