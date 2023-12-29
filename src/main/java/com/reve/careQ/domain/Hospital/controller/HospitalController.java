package com.reve.careQ.domain.Hospital.controller;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Hospital.entity.HospitalDto;
import com.reve.careQ.domain.Hospital.service.HospitalService;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.Reservation.service.ReservationServiceImpl;
import com.reve.careQ.domain.Subject.service.SubjectService;
import com.reve.careQ.global.rq.Rq;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members/subjects/{subject-id}/hospitals")
public class HospitalController {
    private final SubjectService subjectService;
    private final HospitalService hospitalService;
    private final AdminService adminService;
    private final MemberService memberService;
    private final ReservationServiceImpl reservationServiceImpl;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public ModelAndView showHospitals(@PathVariable("subject-id") Long id, Model model){
        ModelAndView mv = new ModelAndView();

        mv.addObject("statesBySubject", adminService.selectAllStates(subjectService.findById(id).get().getCode()));
        mv.addObject("subject",subjectService.findById(id).get());
        mv.setViewName("members/hospitals");

        return mv;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"state"})
    @ResponseBody
    public List<String> getCity(@PathVariable("subject-id") Long id,
                                @RequestParam(name="state",required=false,defaultValue="") String state) {
        List<String> city = adminService.selectAllCities(subjectService.findById(id).get().getCode(), state);

        return city;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"state", "city", "name"})
    @ResponseBody
    public List<HospitalDto> getHospitals(@PathVariable("subject-id") Long id,
                                          @RequestParam(name="state",required=false,defaultValue="") String state,
                                          @RequestParam(name="city",required=false,defaultValue="") String city,
                                          @RequestParam(name="name",required=false,defaultValue="") String name) {
        String subjectCode = subjectService.findById(id).get().getCode();
        List<Hospital> hospitals;

        hospitals = adminService.selectHospitalsByStateAndCity(subjectCode, state, city, name);

        List<HospitalDto> hospitalDto = hospitals.stream()
                .map(hospital -> new HospitalDto(hospital.getId(),hospital.getCode(),hospital.getName()))
                .collect(Collectors.toList());
        return hospitalDto;
    }

    @PreAuthorize("isAuthenticated")
    @GetMapping("/{hospital-id}")
    public ModelAndView showSelect(@PathVariable("subject-id") Long subjectId, @PathVariable("hospital-id") Long hospitalId, Model model) {
        ModelAndView mv = new ModelAndView();

        mv.addObject("subject", subjectService.findById(subjectId).get());
        mv.addObject("hospital", hospitalService.findById(hospitalId).get());
        mv.setViewName("members/select");

        return mv;
    }

    @PreAuthorize("isAuthenticated")
    @PostMapping("/{hospital-id}")
    public String handleSelect(@PathVariable("subject-id") Long subjectId, @PathVariable("hospital-id") Long hospitalId) {

        Optional<Admin> adminOptional = adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId);
        Optional<Member> currentUserOptional = memberService.getCurrentUser();

        if (adminOptional.isPresent() && currentUserOptional.isPresent()) {
            Admin admin = adminOptional.get();
            Member currentUser = currentUserOptional.get();

            RsData<String> reservationStatus = reservationServiceImpl.checkDuplicateReservation(admin.getId(), currentUser.getId());

            if (reservationStatus.isSuccess()) {
                rq.historyBack(reservationStatus.getData());
                return "redirect:/members/subjects/" + subjectId + "/hospitals/" + hospitalId + "/reservations";
            } else {
                return rq.historyBack(reservationStatus);
            }
        }
        return "redirect:/";
    }


}