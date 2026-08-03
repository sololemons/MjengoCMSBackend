package com.storekeeperservice.services;

import com.storekeeperservice.dtos.MaterialDto;
import com.storekeeperservice.dtos.MaterialLogFilterDto;
import com.storekeeperservice.dtos.MaterialRequestDto;
import com.storekeeperservice.entities.Category;
import com.storekeeperservice.entities.Materials;
import com.storekeeperservice.entities.WareHouse;
import com.storekeeperservice.exceptions.UserNotFoundException;
import com.storekeeperservice.interfaces.MaterialNameProjection;
import com.storekeeperservice.records.MaterialResponseDto;
import com.storekeeperservice.repositories.CategoryRepository;
import com.storekeeperservice.repositories.MaterialsRepository;
import com.storekeeperservice.repositories.WareHouseRepository;
import com.storekeeperservice.utilis.MapperDtos;
import com.storekeeperservice.utilis.MaterialSpecification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MaterialService {

  private final MaterialsRepository materialsRepository;
  private final CategoryRepository categoryRepository;
  private final MapperDtos mapperDtos;
  private final WareHouseRepository wareHouseRepository;

  @Transactional
  public String addMaterial(MaterialRequestDto dto) {

    Category category = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(
            () -> new UserNotFoundException("Category not found with ID: " + dto.getCategoryId()));

    WareHouse wareHouse = wareHouseRepository.findByWareHouseName(dto.getWareHouseName())
        .orElseThrow(() -> new UserNotFoundException(
            "Warehouse not found with name: " + dto.getWareHouseName()));

    long quantity = (dto.getQuantity() != null) ? dto.getQuantity() : 0L;
    Long minThreshold = (dto.getMinThreshold() != null) ? dto.getMinThreshold() : 50L;

    Materials newMaterial = Materials.builder()
        .materialName(dto.getMaterialName())
        .quantity(quantity)
        .denomination(dto.getDenomination())
        .brandName(dto.getBrandName())
        .minThreshold(minThreshold)
        .wareHouse(wareHouse)
        .category(category)
        .lastUpdated(LocalDateTime.now())
        .build();

    materialsRepository.save(newMaterial);
    return "Material added successfully";

  }

  @Transactional
  public MaterialResponseDto updateMaterial(Long id, MaterialRequestDto dto) {

    Materials existingMaterial = materialsRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("Material not found with ID: " + id));

    if (dto.getMaterialName() != null) {
      existingMaterial.setMaterialName(dto.getMaterialName());
    }

    if (dto.getQuantity() != null) {
      existingMaterial.setQuantity(dto.getQuantity());
    }
    if (dto.getBrandName() != null) {
      existingMaterial.setBrandName(dto.getBrandName());
    }

    if (dto.getDenomination() != null) {
      existingMaterial.setDenomination(dto.getDenomination());
    }

    if (dto.getMinThreshold() != null) {
      existingMaterial.setMinThreshold(dto.getMinThreshold());
    }

    if (dto.getCategoryId() != null) {
      Category category = categoryRepository.findById(dto.getCategoryId())
          .orElseThrow(() -> new UserNotFoundException(
              "Category not found with ID: " + dto.getCategoryId()));
      existingMaterial.setCategory(category);
    }

    if (dto.getWareHouseName() != null) {
      WareHouse wareHouse = wareHouseRepository.findByWareHouseName(dto.getWareHouseName())
          .orElseThrow(() -> new UserNotFoundException(
              "Warehouse not found with name: " + dto.getWareHouseName()));
      existingMaterial.setWareHouse(wareHouse);
    }

    Materials savedMaterial = materialsRepository.save(existingMaterial);
    String categoryName = (savedMaterial.getCategory() != null)
        ? savedMaterial.getCategory().getCategoryName()
        : null;

    String wareHouseName = (savedMaterial.getWareHouse() != null)
        ? savedMaterial.getWareHouse().getWareHouseName()
        : null;
    return new MaterialResponseDto(
        savedMaterial.getMaterialId(),
        savedMaterial.getMaterialName(),
        savedMaterial.getQuantity(),
        savedMaterial.getDenomination(),
        savedMaterial.getMinThreshold(),
        categoryName,
        wareHouseName,
        savedMaterial.getBrandName()
    );
  }

  @Transactional
  public Page<MaterialDto> getFilteredMaterials(MaterialLogFilterDto filterDto, Pageable pageable) {

    Specification<Materials> spec = MaterialSpecification.filterMaterials(filterDto);

    Page<Materials> materials = materialsRepository.findAll(spec, pageable);

    return materials.map(MapperDtos::mapToDto);
  }

  public List<String> getAllMaterialNames() {
    return materialsRepository.findAllProjectedBy()
        .stream()
        .map(MaterialNameProjection::getMaterialName)
        .collect(Collectors.toList());
  }


}
