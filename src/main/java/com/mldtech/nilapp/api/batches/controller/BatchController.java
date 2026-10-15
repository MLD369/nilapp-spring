package com.mldtech.nilapp.api.batches.controller;

import com.mldtech.nilapp.api.batches.service.BatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/batch")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping("/process-allocations")
    public ResponseEntity<Map<String, Object>> forceRunAllocationBatch() {
        int processedRecords = batchService.executeContributionsAllocationBatch();

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Contributions manually processed and allocated successfully.",
                "processedMasterContributionsCount", processedRecords
        ));
    }
}

