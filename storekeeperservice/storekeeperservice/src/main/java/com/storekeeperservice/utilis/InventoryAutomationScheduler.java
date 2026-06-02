package com.storekeeperservice.utilis;

import com.mjengoshareddtos.UserDto;
import com.storekeeperservice.configuration.retrofit.RetrofitService;
import com.storekeeperservice.dtos.StockStatus;
import com.storekeeperservice.entities.Materials;
import com.storekeeperservice.repositories.MaterialsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryAutomationScheduler {

    private final MaterialsRepository materialsRepository;
    private final RetrofitService retrofitService;
    private final JavaMailSender mailSender;


    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updateStockStatus() {
        log.info("Starting hourly Stock Status update...");

        List<Materials> allMaterials = materialsRepository.findAll();

        for (Materials material : allMaterials) {
            if (material.getQuantity() <= material.getMinThreshold()) {
                material.setStockStatus(StockStatus.UNHEALTHY);
            } else {
                material.setStockStatus(StockStatus.HEALTHY);
            }
        }
        materialsRepository.saveAll(allMaterials);
        log.info("Stock Status update completed.");
    }
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkStockAndSendAlerts() {
        log.info("Starting dynamic low stock check based on individual thresholds...");

        List<Materials> lowStockItems = materialsRepository.findMaterialsBelowThreshold();

        if (lowStockItems.isEmpty()) {
            log.info("Inventory is healthy! No items are below their minimum threshold.");
            return;
        }

        String emailBody = buildAlertMessage(lowStockItems);

        List<UserDto> storekeepers = retrofitService.getStorekeepers();
        List<String> toEmails = storekeepers.stream()
                .map(UserDto::getEmail)
                .collect(Collectors.toList());

        if (toEmails.isEmpty()) {
            log.warn("Low stock detected, but no storekeepers found to email!");
            return;
        }

        sendEmail(toEmails, emailBody);
    }

    private String buildAlertMessage(List<Materials> items) {
        StringBuilder builder = new StringBuilder();
        builder.append("Hello Team,\n\n");
        builder.append("The following materials have dropped below their minimum required threshold and need to be restocked:\n\n");

        for (Materials item : items) {
            builder.append("- ")
                    .append(item.getMaterialName())
                    .append(" (Current Stock: ").append(item.getQuantity())
                    .append(" | Minimum Required: ").append(item.getMinThreshold())
                    .append(" ").append(item.getDenomination())
                    .append(")\n");
        }

        builder.append("\nPlease arrange for deliveries soon.\n\nThank you,\nMjengo System Automated Alert");
        return builder.toString();
    }

    private void sendEmail(List<String> to, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("solomonndimu75@gmail.com");
        message.setTo(to.toArray(new String[0]));
        message.setSubject("URGENT: Low Stock Alert for Mjengo Site");
        message.setText(text);

        mailSender.send(message);
        log.info("Successfully sent low stock alerts to {} storekeepers.", to.size());
    }
    

}