package com.storekeeperservice.services;

import com.storekeeperservice.dtos.RegisterSupplierDto;
import com.storekeeperservice.dtos.SupplierDto;
import com.storekeeperservice.entities.Suppliers;
import com.storekeeperservice.exceptions.MissingFieldException;
import com.storekeeperservice.exceptions.UserNotFoundException;
import com.storekeeperservice.repositories.SuppliersRepository;
import com.storekeeperservice.utilis.MapperDtos;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SuppliersRepository suppliersRepository;
    private final MapperDtos mapperDtos;

    public SupplierDto registerSupplier(RegisterSupplierDto supplierDto) {

        Optional<Suppliers> existingSupplier = suppliersRepository.findBySupplierEmail(supplierDto.getSupplierEmail());
        if (existingSupplier.isPresent()) {
            throw new UserNotFoundException("Supplier with email " + supplierDto.getSupplierEmail() + " already exists");
        }

        Suppliers newSupplier = Suppliers.builder()
                .supplierName(supplierDto.getSupplierName())
                .supplierEmail(supplierDto.getSupplierEmail())
                .contactPerson(supplierDto.getContactPerson())
                .address(supplierDto.getAddress())
                .phoneNumber(supplierDto.getPhoneNumber())
                .build();

        Suppliers savedSupplier = suppliersRepository.save(newSupplier);

        return mapperDtos.mapToDto(savedSupplier);
    }

    public @Nullable SupplierDto updateSupplierInfo(long supplierId, RegisterSupplierDto supplierDto) {
        Suppliers existingSupplier = suppliersRepository.findById(supplierId).orElseThrow(() ->
                new MissingFieldException("Supplier with id " + supplierId + " does not exist"));
        existingSupplier.setSupplierName(supplierDto.getSupplierName());
        existingSupplier.setSupplierEmail(supplierDto.getSupplierEmail());
        existingSupplier.setContactPerson(supplierDto.getContactPerson());
        existingSupplier.setAddress(supplierDto.getAddress());
        existingSupplier.setPhoneNumber(supplierDto.getPhoneNumber());
        Suppliers updatedSupplier = suppliersRepository.save(existingSupplier);
        return mapperDtos.mapToDto(updatedSupplier);
    }

    public @Nullable SupplierDto getSupplierByEmail(String email) {
        Suppliers suppliers = suppliersRepository.findBySupplierEmail(email).orElseThrow(() ->
                new UserNotFoundException("Supplier with email " + email + " does not exist"));
        return mapperDtos.mapToDto(suppliers);
    }

    public @Nullable List<SupplierDto> getAllSuppliers() {
        List<Suppliers> suppliers = suppliersRepository.findAll();
        return mapperDtos.mapSuppliersListToDtoList(suppliers);
    }

    public @Nullable String deleteSupplier(long supplierId) {
        Suppliers suppliers = suppliersRepository.findById(supplierId).orElseThrow(() ->
                new UserNotFoundException("Supplier with id " + supplierId + " does not exist"));
        suppliersRepository.delete(suppliers);
        return "Supplier with id " + supplierId + " was deleted";
    }
}

