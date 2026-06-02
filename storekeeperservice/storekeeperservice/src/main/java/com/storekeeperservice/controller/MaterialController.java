package com.storekeeperservice.controller;

import com.storekeeperservice.dtos.MaterialRequestDto;
import com.storekeeperservice.entities.Materials;
import com.storekeeperservice.services.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping("/add")
    public ResponseEntity<Materials> addMaterial(@RequestBody MaterialRequestDto dto) {
        Materials createdMaterial= materialService.addMaterial(dto);
        return ResponseEntity.ok(createdMaterial);
    }

    @PutMapping("/update/materials")
    public ResponseEntity<Materials> updateMaterial(
            @RequestParam Long id,
            @RequestBody MaterialRequestDto dto) {
        Materials updatedMaterial = materialService.updateMaterial(id, dto);
        return ResponseEntity.ok(updatedMaterial);
    }


}
