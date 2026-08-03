package com.siteoperationsservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectProgressImageDto {

    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private String description;
    private LocalDate imageDate;

}
