package com.storekeeperservice.services;

import com.storekeeperservice.dtos.WareHouseDto;
import com.storekeeperservice.entities.WareHouse;
import com.storekeeperservice.repositories.WareHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WareHouseService {

    private final WareHouseRepository wareHouseRepository;

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

}
