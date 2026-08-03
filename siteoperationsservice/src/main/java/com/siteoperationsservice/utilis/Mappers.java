package com.siteoperationsservice.utilis;

import com.siteoperationsservice.dtos.*;
import com.siteoperationsservice.entities.*;

import java.util.List;
import java.util.stream.Collectors;

public class Mappers {
    // ConstructionProject to ConstructionDto mapping
    public static ConstructionDto toDto(ConstructionProject project) {
        if (project == null) {
            return null;
        }

        return new ConstructionDto(
                project.getConstructionName(),
                project.getDescription(),
                project.getLatitude() != null ? project.getLatitude().toString() : null,
                project.getLongitude() != null ? project.getLongitude().toString() : null,
                project.getStartDate() != null ? project.getStartDate() : null,
                project.getEstimatedEndDate() != null ? project.getEstimatedEndDate() : null,
                project.getOverallProgress(),
                project.getEstimatedEndDate() != null ? project.getEstimatedEndDate() : null,
                false,
                mapProgressImages(project.getProgressImages()),
                mapObjectives(project.getObjectives())
        );
    }

    public static ProjectObjectiveDto toObjectiveDto(ProjectObjective objective) {
        if (objective == null) {
            return null;
        }

        return new ProjectObjectiveDto(
                objective.getDescription(),
                objective.getTitle(),
                objective.getEstimatedDurationDays(),
                objective.isCompleted(),
                objective.getDateCompleted()
        );
    }

    public static ProjectProgressImageDto toProgressImageDto(ProjectProgressImage image) {
        if (image == null) {
            return null;
        }

        return new ProjectProgressImageDto(
                image.getImageUrl(),
                image.getLatitude(),
                image.getLongitude(),
                image.getDescription(),
                image.getImageDate()
        );
    }

    public static List<ProjectObjectiveDto> mapObjectives(List<ProjectObjective> objectives) {
        if (objectives == null) {
            return null;
        }

        return objectives.stream()
                .map(Mappers::toObjectiveDto)
                .collect(Collectors.toList());
    }

    public static List<ProjectProgressImageDto> mapProgressImages(List<ProjectProgressImage> images) {
        if (images == null) {
            return null;
        }

        return images.stream()
                .map(Mappers::toProgressImageDto)
                .collect(Collectors.toList());
    }

    // DailyLogRequestDto to DailyLog mapping
    public static DailyLog toEntity(DailyLogRequestDto dto) {
        if (dto == null) {
            return null;
        }

        DailyLog dailyLog = DailyLog.builder()
                .constructionId(dto.getConstructionId())
                .logDate(dto.getLogDate())
                .build();

        if (dto.getWorkerAttendances() != null) {
            dailyLog.setWorkerAttendances(dto.getWorkerAttendances().stream()
                    .map(wDto -> WorkerAttendance.builder()
                            .workerRole(wDto.getWorkerRole())
                            .quantityReported(wDto.getQuantityReported())
                            .isPremiumDay(wDto.isPremiumDay())
                            .status(wDto.getStatus())
                            .build())
                    .collect(Collectors.toList()));
        }

        if (dto.getMaterialsUsed() != null) {
            dailyLog.setMaterialsUsed(dto.getMaterialsUsed().stream()
                    .map(mDto -> MaterialUsed.builder()
                            .materialName(mDto.getMaterialName())
                            .quantityConsumed(mDto.getQuantityConsumed())
                            .unitOfMeasurement(mDto.getUnitOfMeasurement())
                            .build())
                    .collect(Collectors.toList()));
        }

        return dailyLog;
    }

    // DailyLog to DailyLogResponseDto mapping
    public static DailyLogResponseDto toResponseDto(DailyLog dailyLog) {
        if (dailyLog == null) {
            return null;
        }

        return DailyLogResponseDto.builder()
                .id(dailyLog.getId())
                .constructionId(dailyLog.getConstructionId())
                .logDate(dailyLog.getLogDate())
                .siteEngineerId(Long.valueOf(dailyLog.getSiteEngineerId()))
                .workerAttendances(mapWorkerAttendances(dailyLog.getWorkerAttendances()))
                .materialsUsed(mapMaterialsUsed(dailyLog.getMaterialsUsed()))
                .build();
    }
    public static WorkerAttendanceResponseDto toWorkerAttendanceDto(WorkerAttendance worker) {
        if (worker == null) {
            return null;
        }

        return WorkerAttendanceResponseDto.builder()
                .id(worker.getId())
                .workerRole(worker.getWorkerRole())
                .quantityReported(worker.getQuantityReported())
                .status(worker.getStatus())
                .isPremiumDay(worker.isPremiumDay())
                .build();
    }

    public static MaterialUsedResponseDto toMaterialUsedDto(MaterialUsed material) {
        if (material == null) {
            return null;
        }

        return MaterialUsedResponseDto.builder()
                .id(material.getId())
                .materialName(material.getMaterialName())
                .quantityConsumed(material.getQuantityConsumed())
                .unitOfMeasurement(material.getUnitOfMeasurement())
                .build();
    }

    public static List<WorkerAttendanceResponseDto> mapWorkerAttendances(List<WorkerAttendance> workers) {
        if (workers == null) {
            return null;
        }

        return workers.stream()
                .map(Mappers::toWorkerAttendanceDto)
                .collect(Collectors.toList());
    }

    public static List<MaterialUsedResponseDto> mapMaterialsUsed(List<MaterialUsed> materials) {
        if (materials == null) {
            return null;
        }

        return materials.stream()
                .map(Mappers::toMaterialUsedDto)
                .collect(Collectors.toList());
    }

}