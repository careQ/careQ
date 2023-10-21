package com.reve.careQ.domain.Hospital.controller;

import com.reve.careQ.domain.Subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class HospitalController {
    private final SubjectService subjectService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/members/subjects/{subject-id}/hospitals")
    public ModelAndView showHospitals(@PathVariable("subject-id") Long id, Model model){
        ModelAndView mv = new ModelAndView();
        mv.addObject("subject",subjectService.findById(id).getData().get());
        mv.setViewName("members/hospitals");
        return mv;
    }
}
