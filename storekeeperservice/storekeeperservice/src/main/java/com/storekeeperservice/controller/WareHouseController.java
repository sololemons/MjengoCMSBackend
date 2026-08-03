package com.storekeeperservice.controller;

import com.storekeeperservice.dtos.WareHouseDto;
import com.storekeeperservice.services.WareHouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/store/warehouse")
@RestController
@RequiredArgsConstructor
public class WareHouseController {

    private final WareHouseService wareHouseService;

    @PostMapping("/add")
    public ResponseEntity<String> addWareHouse(@RequestBody WareHouseDto wareHouseDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wareHouseService.addWareHouse(wareHouseDto));
    }

    @GetMapping("/get")
    public ResponseEntity<List<WareHouseDto>> getWareHouses() {
        return ResponseEntity.ok(wareHouseService.getWareHouses());
    }


}
