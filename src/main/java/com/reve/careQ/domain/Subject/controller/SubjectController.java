package com.reve.careQ.domain.Subject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class SubjectController {
    @GetMapping("/members/subjects")
    public String subject() {
        return "members/subjects";
    }
}
