package com.storekeeperservice.controller;

import com.storekeeperservice.dtos.RegisterSupplierDto;
import com.storekeeperservice.dtos.SupplierDto;
import com.storekeeperservice.dtos.SupplierFilterDto;
import com.storekeeperservice.services.SupplierService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class SupplierController {

  private final SupplierService supplierService;


  @PostMapping("/register/supplier")
  public ResponseEntity<SupplierDto> registerSupplier(@RequestBody RegisterSupplierDto supplier) {
    return ResponseEntity.ok(supplierService.registerSupplier(supplier));
  }

  @PutMapping("/update/supplier/info")
  public ResponseEntity<SupplierDto> updateSupplierInfo(@RequestParam long supplierId,
      @RequestBody RegisterSupplierDto supplierDto) {
    return ResponseEntity.ok(supplierService.updateSupplierInfo(supplierId, supplierDto));
  }

  @GetMapping("/get/supplier/by/email")
  public ResponseEntity<SupplierDto> getSupplierByEmail(@RequestParam String email) {
    return ResponseEntity.ok(supplierService.getSupplierByEmail(email));
  }

  @GetMapping("/get/all/suppliers")
  public ResponseEntity<List<SupplierDto>> getAllSuppliers() {
    return ResponseEntity.ok(supplierService.getAllSuppliers());
  }

  @GetMapping("/filtered/suppliers")
  public ResponseEntity<Page<SupplierDto>> getMaterials(
      @ModelAttribute SupplierFilterDto filterDto,
      @PageableDefault(page = 0, size = 10, sort = "lastUpdated", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<SupplierDto> paginatedLogs = supplierService.getFilteredSuppliers(filterDto, pageable);

    return ResponseEntity.status(200).body(paginatedLogs);
  }

  @DeleteMapping("/delete/supplier")
  public ResponseEntity<String> deleteSupplier(@RequestParam long supplierId) {
    return ResponseEntity.ok(supplierService.deleteSupplier(supplierId));
  }


}
