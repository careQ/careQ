package com.reve.careQ.domain.RegisterChart.controller;

import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.dto.RegisterQueueInfoDto;
import com.reve.careQ.domain.RegisterChart.service.RegisterChartService;
import com.reve.careQ.global.rq.Rq;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;

@Controller
@RequestMapping("/members/subjects/{subject-id}/hospitals/{hospital-id}/queues")
@RequiredArgsConstructor
public class RegisterChartController {
    private final RegisterChartService registerChartService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ModelAndView showQueue(@PathVariable("subject-id") Long subjectId, @PathVariable("hospital-id") Long hospitalId){
        ModelAndView mv = new ModelAndView();
        RegisterQueueInfoDto registerQueueInfo = registerChartService.getRegisterQueueInfo(hospitalId, subjectId);
        mv.addObject("registerQueueInfo", registerQueueInfo);
        mv.setViewName("members/queues");
        return mv;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public String createRegister(@PathVariable("subject-id") Long subjectId,
                                 @PathVariable("hospital-id") Long hospitalId) {

        RsData<RegisterChart> registerChartRs = registerChartService.insert(hospitalId, subjectId);

        if (registerChartRs.isSuccess()) {
            return "redirect:/members/subjects/" + subjectId + "/hospitals/" + hospitalId + "/queues";
        } else {
            return rq.historyBack(registerChartRs);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    public ResponseEntity<?> deleteRegister(@PathVariable("subject-id") Long subjectId,
                                            @PathVariable("hospital-id") Long hospitalId) {
        try {
            registerChartService.processRegisterChart(hospitalId, subjectId);
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .location(URI.create("/members"))
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RsData.failOf("F-0", e.getMessage()));
        }
    }
}
