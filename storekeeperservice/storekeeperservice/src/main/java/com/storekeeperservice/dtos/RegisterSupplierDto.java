package com.storekeeperservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterSupplierDto {

    private String supplierName;
    private String contactPerson;
    private String phoneNumber;
    private String supplierEmail;
    private String address;
}
