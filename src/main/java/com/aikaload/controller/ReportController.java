package com.aikaload.controller;

import com.aikaload.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/get-transaction-history-by-userId/{userId}")
    public ResponseEntity getTransactionHistoryByUserId(@PathVariable Long userId){
        return reportService.getTransactionHistoryByUserId(userId);
    }

    @GetMapping("/get-settlement-history-by-truckOwnerId/{truckOwnerId}")
    public ResponseEntity getSettlementHistoryByTruckOwnerId(@PathVariable Long truckOwnerId){
        return reportService.getSettlementHistoryByTruckOwnerId(truckOwnerId);
    }
}
