package com.mldtech.nilapp.api.payout_batches.controller;

import com.mldtech.nilapp.api.payout_batches.model.PayoutBatch;
import com.mldtech.nilapp.api.payout_batches.service.PayoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/payouts")
public class PayoutController {

    private final PayoutService payoutService;

    public PayoutController(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

    /**
     * Preview endpoint to audit exactly how many coins, steps, and dollars are calculated.
     */
    @GetMapping("/preview/{goalInstanceId}")
    public ResponseEntity<Map<String, Object>> previewGoalPayoutDetails(@PathVariable("goalInstanceId") Long goalInstanceId) {
        return ResponseEntity.ok(payoutService.getPayoutPreviewMetrics(goalInstanceId));
    }

    /**
     * Execution settlement button submission router endpoint.
     */
    @PostMapping("/settle/{goalInstanceId}")
    public ResponseEntity<PayoutBatch> settleGoalCampaign(
            @PathVariable("goalInstanceId") Long goalInstanceId,
            @RequestBody Map<String, String> payload) {

        String method = payload.getOrDefault("paymentMethod", "ACH");
        String tracking = payload.getOrDefault("trackingNumber", "TRACE_PENDING");

        PayoutBatch finalReceipt = payoutService.executeFinancialSettlement(goalInstanceId, method, tracking);
        return ResponseEntity.ok(finalReceipt);
    }
    /**
     * REST Endpoint called by the administrative treasury dashboard.
     * Retrieves all finalized entity payout batches that are legally/financially cleared for bank release.
     *
     * @return a response entity containing the list of matured payout records
     */
    @GetMapping("/matured-releases")
    public ResponseEntity<List<PayoutBatch>> getMaturedPayoutReleases() {
        List<PayoutBatch> maturedPayouts = payoutService.getMaturedPendingPayouts();
        return ResponseEntity.ok(maturedPayouts);
    }


}

