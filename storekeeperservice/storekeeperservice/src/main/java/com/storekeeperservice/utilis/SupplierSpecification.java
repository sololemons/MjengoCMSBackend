package com.storekeeperservice.utilis;

import com.storekeeperservice.dtos.SupplierFilterDto;
import com.storekeeperservice.entities.Suppliers;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class SupplierSpecification {

  public static Specification<Suppliers> filterSuppliers(SupplierFilterDto logFilterDto) {

    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (logFilterDto.getSupplierEmail() != null && !logFilterDto.getSupplierEmail().isEmpty()) {
        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("supplierEmail")),
            "%" + logFilterDto.getSupplierEmail().toLowerCase() + "%"));
      }

      if (logFilterDto.getPhoneNumber() != null && !logFilterDto.getPhoneNumber().isEmpty()) {
        predicates.add(
            criteriaBuilder.equal(root.get("phoneNumber"), logFilterDto.getPhoneNumber()));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }


}



