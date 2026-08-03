package com.storekeeperservice.repositories;

import com.storekeeperservice.entities.Suppliers;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SuppliersRepository extends JpaRepository<Suppliers, Long>,
    JpaSpecificationExecutor<Suppliers> {

  Optional<Suppliers> findBySupplierEmail(String supplierEmail);

  Optional<Suppliers> findBySupplierName(String supplierName);

}
