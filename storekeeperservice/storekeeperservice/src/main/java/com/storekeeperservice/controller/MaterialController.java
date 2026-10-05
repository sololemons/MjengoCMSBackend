package com.storekeeperservice.controller;

import com.storekeeperservice.dtos.MaterialDto;
import com.storekeeperservice.dtos.MaterialLogFilterDto;
import com.storekeeperservice.dtos.MaterialRequestDto;
import com.storekeeperservice.interfaces.MaterialNameProjection;
import com.storekeeperservice.records.MaterialResponseDto;
import com.storekeeperservice.services.MaterialService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store/materials")
@RequiredArgsConstructor
public class MaterialController {

  private final MaterialService materialService;

  @PostMapping("/add")
  public ResponseEntity<String> addMaterial(@RequestBody MaterialRequestDto dto) {
    String createdMaterial = materialService.addMaterial(dto);
    return ResponseEntity.ok(createdMaterial);
  }

  @PatchMapping("/update/materials")
  public ResponseEntity<MaterialResponseDto> updateMaterial(
      @RequestParam Long id,
      @RequestBody MaterialRequestDto dto) {
    MaterialResponseDto updatedMaterial = materialService.updateMaterial(id, dto);
    return ResponseEntity.ok(updatedMaterial);
  }

  @GetMapping("/filtered")
  public ResponseEntity<Page<MaterialDto>> getMaterials(
      @ModelAttribute MaterialLogFilterDto filterDto,
      @PageableDefault(page = 0, size = 10, sort = "lastUpdated", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<MaterialDto> paginatedLogs = materialService.getFilteredMaterials(filterDto, pageable);

    return ResponseEntity.status(200).body(paginatedLogs);
  }

  @GetMapping("/names/id")
  public ResponseEntity<List<MaterialNameProjection>> getMaterialNamesAndIds() {
    return ResponseEntity.ok(materialService.getAllMaterialNamesAndId());
  }


}

