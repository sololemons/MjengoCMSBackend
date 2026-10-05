package com.mjengoshareddtos;

import lombok.Data;

@Data
public class UserDto {

    private String email;
    private Long userId;
    private String phoneNumber;
    private String role;
    private String assignedSite;
}
