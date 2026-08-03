package com.siteoperationsservice.controllers;

import com.siteoperationsservice.dtos.DailyLogFilterDto;
import com.siteoperationsservice.dtos.DailyLogRequestDto;
import com.siteoperationsservice.dtos.DailyLogResponseDto;
import com.siteoperationsservice.services.SiteActivitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/site/activities")
@RequiredArgsConstructor
public class SiteActivitiesController {

    private final SiteActivitiesService siteActivitiesService;

    @PostMapping("/add-log")
    public ResponseEntity<String> addLog(@RequestBody DailyLogRequestDto dailyLogRequestDto) {
        return ResponseEntity.status(201).body(siteActivitiesService.addDailyLog(dailyLogRequestDto));

    }

    @PatchMapping("/update-log")
    public ResponseEntity<String> updateLog(@RequestParam Long logId, @RequestBody DailyLogRequestDto dailyLogRequestDto) {
        return ResponseEntity.status(200).body(siteActivitiesService.updateDailyLog(logId, dailyLogRequestDto));
    }

    @GetMapping("/logs")
    public ResponseEntity<Page<DailyLogResponseDto>> getLogsFilteredAndPaginated(
            @ModelAttribute DailyLogFilterDto filterDto,
            @PageableDefault(page = 0, size = 10, sort = "logDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<DailyLogResponseDto> paginatedLogs = siteActivitiesService.getFilteredLogs(filterDto, pageable);

        return ResponseEntity.status(200).body(paginatedLogs);
    }

}
