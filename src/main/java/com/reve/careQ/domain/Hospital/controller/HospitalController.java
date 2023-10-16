package com.reve.careQ.domain.Hospital.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HospitalController {
    @GetMapping("/members/hospitals")
    public String hospital() {
        return "members/hospitals";
    }
}
