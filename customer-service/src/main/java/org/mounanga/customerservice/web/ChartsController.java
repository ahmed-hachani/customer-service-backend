package org.mounanga.customerservice.web;

import org.mounanga.customerservice.service.implementation.CreditRequestServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/charts")
public class ChartsController {
    private final CreditRequestServiceImpl creditRequestService;

    public ChartsController(CreditRequestServiceImpl creditRequestService) {
        this.creditRequestService = creditRequestService;
    }

    @GetMapping("/stats/requests-per-month")
    public ResponseEntity<Map<String, Long>> getRequestStats() {
        return ResponseEntity.ok(creditRequestService.getMonthlyRequestStats());
    }
    @GetMapping("/stats/approval-status")
    public ResponseEntity<Map<String, Long>> getApprovalStatusStats() {
        Map<String, Long> stats = creditRequestService.getApprovalStatusStats();
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/stats/final-decision")
    public ResponseEntity<Map<String, Long>> getFinalDecisionStats() {
        Map<String, Long> stats = creditRequestService.getFinalDecisionStats();
        return ResponseEntity.ok(stats);
    }
}
