package com.reve.careQ.domain.Admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/")
    public String showAdminsHome() {
        return "admins/admins-home";
    }

}