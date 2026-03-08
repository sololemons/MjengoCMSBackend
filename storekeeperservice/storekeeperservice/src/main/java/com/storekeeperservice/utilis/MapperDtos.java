package com.storekeeperservice.utilis;

import com.storekeeperservice.dtos.CreateCategoryDto;
import com.storekeeperservice.dtos.SupplierDto;
import com.storekeeperservice.entities.Category;
import com.storekeeperservice.entities.Suppliers;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MapperDtos {

    public CreateCategoryDto mapToDto(Category category) {
        CreateCategoryDto dto = new CreateCategoryDto();
        dto.setCategoryName(category.getCategoryName());
        dto.setCategoryDescription(category.getDescription());
        return dto;
    }

    public List<CreateCategoryDto> mapCategoryListToDtoList(List<Category> categories) {
        return categories.stream()
                .map(this::mapToDto)
                .toList();
    }
    public SupplierDto mapToDto(Suppliers suppliers) {
        SupplierDto dto = new SupplierDto();
        dto.setSupplierName(suppliers.getSupplierName());
        dto.setSupplierId(suppliers.getSupplierId());
        dto.setAddress(suppliers.getAddress());
        dto.setPhoneNumber(suppliers.getPhoneNumber());
        dto.setSupplierEmail(suppliers.getSupplierEmail());
        dto.setContactPerson(suppliers.getContactPerson());
        return dto;
    }

    public List<SupplierDto> mapSuppliersListToDtoList(List<Suppliers> suppliers) {
        return suppliers.stream()
                .map(this::mapToDto)
                .toList();
    }
}
