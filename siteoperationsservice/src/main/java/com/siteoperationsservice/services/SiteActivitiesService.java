package com.siteoperationsservice.services;

import com.siteoperationsservice.dtos.DailyLogFilterDto;
import com.siteoperationsservice.dtos.DailyLogRequestDto;
import com.siteoperationsservice.dtos.DailyLogResponseDto;
import com.siteoperationsservice.entities.DailyLog;
import com.siteoperationsservice.exceptions.DuplicateDailyLogException;
import com.siteoperationsservice.exceptions.ResourceNotFoundException;
import com.siteoperationsservice.repositories.DailyLogRepository;
import com.siteoperationsservice.utilis.ConstructionSpecifications;
import com.siteoperationsservice.utilis.Mappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteActivitiesService {
    private final DailyLogRepository dailyLogRepository;

    public String addDailyLog(DailyLogRequestDto dailyLogRequestDto) {

        boolean logExists = dailyLogRepository.existsByConstructionIdAndLogDate(
                dailyLogRequestDto.getConstructionId(),
                dailyLogRequestDto.getLogDate()
        );
        if (logExists) {
            log.warn("Duplicate entry rejection: Construction ID {} already has a log for {}",
                    dailyLogRequestDto.getConstructionId(), dailyLogRequestDto.getLogDate());
            throw new DuplicateDailyLogException(String.format("A daily log for construction ID %s on date %s already exists.",
                    dailyLogRequestDto.getConstructionId(), dailyLogRequestDto.getLogDate())

            );
        }
        DailyLog dailyLogEntity = Mappers.toEntity(dailyLogRequestDto);
        dailyLogRepository.save(dailyLogEntity);
        return "Daily log added successfully";

    }

    @Transactional
    public String updateDailyLog(Long logId, DailyLogRequestDto requestDto) {
        DailyLog existingLog = dailyLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Daily log not found with ID: " + logId));

        LocalDate today = LocalDate.now();
        if (today.isAfter(existingLog.getLogDate())) {
            log.warn("Update blocked: Attempted to edit Log ID {} on {} but log date is frozen on {}",
                    logId, today, existingLog.getLogDate());
            throw new RuntimeException();
        }

        existingLog.getWorkerAttendances().clear();
        existingLog.getMaterialsUsed().clear();
        dailyLogRepository.saveAndFlush(existingLog);

        DailyLog updatedData = Mappers.toEntity(requestDto);

        if (updatedData.getWorkerAttendances() != null) {
            existingLog.getWorkerAttendances().addAll(updatedData.getWorkerAttendances());
        }
        if (updatedData.getMaterialsUsed() != null) {
            existingLog.getMaterialsUsed().addAll(updatedData.getMaterialsUsed());
        }
        dailyLogRepository.save(existingLog);

        return "Daily log ID " + logId + " updated successfully before the midnight lock.";
    }

    public Page<DailyLogResponseDto> getFilteredLogs(DailyLogFilterDto filters, Pageable pageable) {
        Specification<DailyLog> spec = ConstructionSpecifications.filterLogs(filters);

        Page<DailyLog> rawEntityPage = dailyLogRepository.findAll(spec, pageable);

        return rawEntityPage.map(Mappers::toResponseDto);
    }

}
