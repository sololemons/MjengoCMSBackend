package com.authenticationservice.authentication.utilis;

import com.authenticationservice.authentication.entities.Users;
import com.mjengoshareddtos.UserDto;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class DtoMapper {
  //map single entity
    public UserDto mapToDto(Users user) {
        UserDto dto = new UserDto();
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }
    //map list
    public List<UserDto> mapToDto(List<Users> users) {
        return users.stream()
                .map(this::mapToDto)
                .toList();
    }
}
