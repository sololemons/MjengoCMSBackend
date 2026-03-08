package com.storekeeperservice.controller;

import com.storekeeperservice.dtos.RegisterSupplierDto;
import com.storekeeperservice.dtos.SupplierDto;
import com.storekeeperservice.services.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<SupplierDto> updateSupplierInfo(@RequestParam long supplierId, @RequestBody RegisterSupplierDto supplierDto){
        return ResponseEntity.ok(supplierService.updateSupplierInfo(supplierId,supplierDto));
    }
    @GetMapping("/get/supplier/by/email")
    public ResponseEntity<SupplierDto> getSupplierByEmail(@RequestParam String email){
        return ResponseEntity.ok(supplierService.getSupplierByEmail(email));
    }
    @GetMapping("/get/all/suppliers")
    public ResponseEntity<List<SupplierDto>> getAllSuppliers(){
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }
    @DeleteMapping("/delete/supplier")
    public ResponseEntity<String> deleteSupplier(@RequestParam long supplierId){
        return ResponseEntity.ok(supplierService.deleteSupplier(supplierId));
    }





}
