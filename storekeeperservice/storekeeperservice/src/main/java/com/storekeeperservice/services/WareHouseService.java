package com.storekeeperservice.services;

import com.storekeeperservice.dtos.WareHouseDto;
import com.storekeeperservice.entities.WareHouse;
import com.storekeeperservice.repositories.WareHouseRepository;
import com.storekeeperservice.utilis.MapperDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WareHouseService {

    private final WareHouseRepository wareHouseRepository;
    private final MapperDtos mapperDtos;

    public String addWareHouse(WareHouseDto wareHouseDto) {
        Optional<WareHouse> wareHouse = wareHouseRepository.findByWareHouseName(wareHouseDto.getWareHouseName());
        if (wareHouse.isPresent()) {
            return "WareHouse already exists";
        } else {
            WareHouse newWareHouse = WareHouse.builder()
                    .wareHouseName(wareHouseDto.getWareHouseName())
                    .location(wareHouseDto.getLocation())
                    .build();

            wareHouseRepository.save(newWareHouse);
            return "WareHouse added successfully";
        }
    }

    public List<WareHouseDto> getWareHouses() {
        List<WareHouse> wareHouse = wareHouseRepository.findAll();
        return wareHouse.stream()
                .map(mapperDtos::mapToDto)
                .collect(Collectors.toList());
    }


}
