package com.storekeeperservice.controller;

import com.storekeeperservice.utilis.InventoryAutomationScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class TestController {

    private final InventoryAutomationScheduler stockAlertScheduler;

    @PostMapping("/test-alert")
    public ResponseEntity<String> triggerManualAlert() {
        try {
            stockAlertScheduler.checkStockAndSendAlerts();

            return ResponseEntity.ok("Manual stock alert triggered successfully! Go check your Gmail.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send alert: " + e.getMessage());
        }
    }
}