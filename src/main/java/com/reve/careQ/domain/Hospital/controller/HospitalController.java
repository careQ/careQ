package com.reve.careQ.domain.Hospital.controller;

import com.reve.careQ.domain.Admin.repository.AdminRepository;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Hospital.entity.HospitalDto;
import com.reve.careQ.domain.Hospital.service.HospitalService;
import com.reve.careQ.domain.Subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members/subjects/{subject-id}/hospitals")
public class HospitalController {
    private final SubjectService subjectService;

    private final HospitalService hospitalService;

    private final AdminRepository adminRepository;

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public ModelAndView showHospitals(@PathVariable("subject-id") Long id, Model model){
        ModelAndView mv = new ModelAndView();

        mv.addObject("statesBySubject", adminRepository.selectAllStates(subjectService.findById(id).getData().get().getCode()));
        mv.addObject("subject",subjectService.findById(id).getData().get());
        mv.setViewName("members/hospitals");

        return mv;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"state"})
    @ResponseBody
    public List<String> getCity(@PathVariable("subject-id") Long id,
                                @RequestParam(name="state",required=false,defaultValue="") String state) {
        List<String> city = adminRepository.selectAllCities(subjectService.findById(id).getData().get().getCode(), state);

        return city;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"state", "city", "name"})
    @ResponseBody
    public List<HospitalDto> getHospitals(@PathVariable("subject-id") Long id,
                                          @RequestParam(name="state",required=false,defaultValue="") String state,
                                          @RequestParam(name="city",required=false,defaultValue="") String city,
                                          @RequestParam(name="name",required=false,defaultValue="") String name) {
        String subjectCode = subjectService.findById(id).getData().get().getCode();
        List<Hospital> hospitals;

        hospitals = adminRepository.selectHospitalsByStateAndCity(subjectCode, state, city, name);

        List<HospitalDto> hospitalDto = hospitals.stream()
                .map(hospital -> new HospitalDto(hospital.getId(),hospital.getCode(),hospital.getName()))
                .collect(Collectors.toList());
        return hospitalDto;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{hospital-id}")
    public ModelAndView showSelect(@PathVariable("subject-id") Long subjectId, @PathVariable("hospital-id") Long hospitalId, Model model){
        ModelAndView mv = new ModelAndView();

        mv.addObject("subject",subjectService.findById(subjectId).getData().get());
        mv.addObject("hospital",hospitalService.findById(hospitalId).getData().get());
        mv.setViewName("members/select");

        return mv;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{hospital-id}/queues")
    public ModelAndView showQueue(@PathVariable("subject-id") Long subjectId, @PathVariable("hospital-id") Long hospitalId, Model model){
        ModelAndView mv = new ModelAndView();

        mv.addObject("subject",subjectService.findById(subjectId).getData().get());
        mv.addObject("hospital",hospitalService.findById(hospitalId).getData().get());
        mv.setViewName("members/queues");

        return mv;
    }

}
