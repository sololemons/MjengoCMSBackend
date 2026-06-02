package com.mjengoshareddtos;

import lombok.Data;

@Data
public class UserDto {

    private String email;
    private String phoneNumber;
    private String role;
    private String assignedSite;
}
