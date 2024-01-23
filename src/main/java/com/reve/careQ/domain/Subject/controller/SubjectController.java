package com.reve.careQ.domain.Subject.controller;

import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Subject.entity.Subject;
import com.reve.careQ.domain.Subject.dto.SubjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/members/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final AdminService adminService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public String showSubjects() {
        return "members/subjects";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"name"})
    @ResponseBody
    public List<SubjectDto> searchSubjects(@RequestParam(name="name",required=false,defaultValue="") String name) {
        List<Subject> subjects = adminService.selectSubjectsByName(name);

        List<SubjectDto> subjectDto = subjects.stream()
                .map(subject -> new SubjectDto(subject.getId(),subject.getCode(),subject.getName()))
                .collect(Collectors.toList());
        return subjectDto;
    }
}
