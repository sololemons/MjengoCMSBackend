package com.siteoperationsservice.schedulers;

import com.siteoperationsservice.dtos.DailyLogResponseDto;
import com.siteoperationsservice.entities.DailyLog;
import com.siteoperationsservice.repositories.DailyLogRepository;
import com.siteoperationsservice.services.PdfReportService;
import com.siteoperationsservice.utilis.Mappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulingService {

    private final DailyLogRepository dailyLogRepository;
    private final PdfReportService pdfReportService;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(readOnly = true)
    public void sendDailyLogReport() {
        log.info("Starting daily log report job... {}", LocalDateTime.now());

        LocalDate targetDate = LocalDate.now().minusDays(1);

        Optional<DailyLog> optionalDaily = dailyLogRepository.findByLogDateAndIsLogSent(targetDate, false);

        if (optionalDaily.isPresent()) {
            DailyLog dailyLog = optionalDaily.get();
            DailyLogResponseDto dto = Mappers.toResponseDto(dailyLog);

            try {
                String COMPANY_EMAIL = "management@yourcompany.com";
                pdfReportService.generateAndSendPdf(COMPANY_EMAIL, dto);
                dailyLog.setLogSent(true);
                dailyLogRepository.save(dailyLog);

                log.info("Successfully processed and sent report for {}", targetDate);

            } catch (Exception e) {
                log.error(" Failed to generate/send PDF report for date: {}", targetDate, e);
            }

        } else {
            log.info("No unsent daily logs found for date: {}", targetDate);
        }
    }
}