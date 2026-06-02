package com.storekeeperservice.services;

import com.storekeeperservice.dtos.MachineryCondition;
import com.storekeeperservice.dtos.MaterialRequestDto;
import com.storekeeperservice.entities.Category;
import com.storekeeperservice.entities.Materials;
import com.storekeeperservice.exceptions.UserNotFoundException;
import com.storekeeperservice.repositories.CategoryRepository;
import com.storekeeperservice.repositories.MaterialsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MaterialService {
    private final MaterialsRepository materialsRepository;
    private final CategoryRepository categoryRepository;
    public Materials addMaterial(MaterialRequestDto dto) {

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new UserNotFoundException("Category not found with ID: " + dto.getCategoryId()));

        Materials newMaterial = Materials.builder()
                .materialName(dto.getMaterialName())
                .quantity(dto.getQuantity())
                .denomination(dto.getDenomination())
                .minThreshold(dto.getMinimumStockLevel())
                .category(category)
                .quantity(0L)
                .machineryCondition(parseMachineryCondition(dto.getMachineryCondition()))
                .build();

        return materialsRepository.save(newMaterial);
    }

    @Transactional
    public Materials updateMaterial(Long materialId, MaterialRequestDto dto) {

        Materials existingMaterial = materialsRepository.findById(materialId)
                .orElseThrow(() -> new UserNotFoundException("Material not found with ID: " + materialId));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new UserNotFoundException("Category not found with ID: " + dto.getCategoryId()));

        existingMaterial.setMaterialName(dto.getMaterialName());
        existingMaterial.setQuantity(dto.getQuantity());
        existingMaterial.setDenomination(dto.getDenomination());
        existingMaterial.setMinThreshold(dto.getMinimumStockLevel());
        existingMaterial.setCategory(category);
        existingMaterial.setMachineryCondition(parseMachineryCondition(dto.getMachineryCondition()));


        return materialsRepository.save(existingMaterial);
    }
    private MachineryCondition parseMachineryCondition(String conditionStr) {
        if (conditionStr == null || conditionStr.isBlank()) {
            return null;
        }

        try {
            return MachineryCondition.valueOf(conditionStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UserNotFoundException("Invalid machinery condition provided: " + conditionStr);
        }
    }
}
