package com.storekeeperservice.utilis;
import com.storekeeperservice.dtos.MaterialTrackingFilterDto;
import com.storekeeperservice.entities.MaterialTracking;
import com.storekeeperservice.entities.Materials;
import com.storekeeperservice.entities.Suppliers;
import com.storekeeperservice.entities.WareHouse;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MaterialTrackingSpecification {

    public static Specification<MaterialTracking> filterMaterialTracking(
  MaterialTrackingFilterDto logFilterDto
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (logFilterDto.getSupplierName() != null && !logFilterDto.getSupplierName().isEmpty()) {
                Join<MaterialTracking,Suppliers> supplierJoin = root.join("suppliers", JoinType.INNER);
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(supplierJoin.get("supplierName")),
                                "%" + logFilterDto.getSupplierName().toLowerCase() + "%"
                        )
                );
            }


            if (logFilterDto.getMaterialName() != null) {
                predicates.add(criteriaBuilder.equal(root.get("materials").get("materialName"), logFilterDto.getMaterialName()));
            }

            if (logFilterDto.getMovementType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("materialMovementType"), logFilterDto.getMovementType()));
            }

            if (logFilterDto.getRecordedBy() != null && !logFilterDto.getRecordedBy().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("recordedBy"), logFilterDto.getRecordedBy()));
            }

            if (logFilterDto.getStartDate() != null && logFilterDto.getEndDate() != null) {
                predicates.add(criteriaBuilder.between(root.get("timestamp"), logFilterDto.getStartDate(), logFilterDto.getEndDate()));
            } else if (logFilterDto.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), logFilterDto.getStartDate()));
            } else if (logFilterDto.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), logFilterDto.getEndDate()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}