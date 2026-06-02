package com.storekeeperservice.configuration.retrofit;

import com.mjengoshareddtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class RetrofitController {
    private final RetrofitService retrofitService;

    @GetMapping("/storekeepers")
    public ResponseEntity<List<UserDto>> fetchStorekeepers() {
        List<UserDto> storekeepers = retrofitService.getStorekeepers();

        return ResponseEntity.ok(storekeepers);
    }
}