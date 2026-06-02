package com.storekeeperservice.utilis;

import com.storekeeperservice.dtos.MaterialMovementType;
import com.storekeeperservice.entities.MaterialTracking;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MaterialTrackingSpecification {

    public static Specification<MaterialTracking> hasSupplierId(Long supplierId) {
        return (root, query, cb) ->
                (supplierId == null)
                        ? null
                        : cb.equal(root.get("suppliers").get("id"), supplierId);
    }

    public static Specification<MaterialTracking> hasMaterialId(Long materialId) {
        return (root, query, cb) ->
                (materialId == null)
                        ? null
                        : cb.equal(root.get("materials").get("id"), materialId);
    }

    public static Specification<MaterialTracking> hasMovementType(MaterialMovementType type) {
        return (root, query, cb) ->
                (type == null)
                        ? null
                        : cb.equal(root.get("materialMovementType"), type);
    }

    public static Specification<MaterialTracking> hasRecordedBy(String recordedBy) {
        return (root, query, cb) ->
                (recordedBy == null || recordedBy.isBlank())
                        ? null
                        : cb.equal(root.get("recordedBy"), recordedBy);
    }

    public static Specification<MaterialTracking> isBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) return null;
            if (startDate == null) return cb.lessThanOrEqualTo(root.get("timestamp"), endDate);
            if (endDate == null) return cb.greaterThanOrEqualTo(root.get("timestamp"), startDate);

            return cb.between(root.get("timestamp"), startDate, endDate);
        };
    }
}