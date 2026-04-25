//package com.mldtech.nilapp.api.contributions.children.ContributionStatus.controller;
//
//import com.mldtech.nilapp.api.contributions.children.ContributionStatus.model.ContributionStatus;
//import com.mldtech.nilapp.api.contributions.children.ContributionStatus.service.ContributionStatusService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/contribution-status")
//@RequiredArgsConstructor
//public class ContributionStatusController {
//
//    private final ContributionStatusService service;
//
//    @GetMapping
//    public List<ContributionStatus> getAllStatuses() {
//        return service.getAllStatuses();
//    }
//
//    @PostMapping
//    public ContributionStatus createStatus(@RequestBody ContributionStatus status) {
//        return service.createStatus(status);
//    }
//}
//
