package com.storekeeperservice.utilis;

import com.storekeeperservice.dtos.MaterialLogFilterDto;
import com.storekeeperservice.entities.Category;
import com.storekeeperservice.entities.Materials;
import com.storekeeperservice.entities.WareHouse;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MaterialSpecification {
    public static Specification<Materials>filterMaterials(MaterialLogFilterDto logFilterDto){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (logFilterDto.getMaterialName() != null && !logFilterDto.getMaterialName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("materialName")), "%" + logFilterDto.getMaterialName().toLowerCase() + "%"));
            }

            if (logFilterDto.getCategoryName() != null && !logFilterDto.getCategoryName().isEmpty()) {
                Join<Materials, Category> categoryJoin = root.join("category", JoinType.INNER);
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(categoryJoin.get("categoryName")),
                                "%" + logFilterDto.getCategoryName().toLowerCase() + "%"
                        )
                );
            }

            if (logFilterDto.getWareHouseName() != null && !logFilterDto.getWareHouseName().isEmpty()) {
                Join<Materials, WareHouse> wareHouseJoin = root.join("wareHouse", JoinType.INNER);
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(wareHouseJoin.get("wareHouseName")),
                                "%" + logFilterDto.getWareHouseName().toLowerCase() + "%"
                        )
                );
            }

            if(logFilterDto.getStockStatus() != null && !logFilterDto.getStockStatus().isEmpty()){
                predicates.add(criteriaBuilder.equal(root.get("stockStatus"), logFilterDto.getStockStatus()));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }



}
