package com.storekeeperservice.repositories;

import com.storekeeperservice.entities.Suppliers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuppliersRepository extends JpaRepository<Suppliers, Long> {
    Optional<Suppliers> findBySupplierEmail(String supplierEmail);
}
