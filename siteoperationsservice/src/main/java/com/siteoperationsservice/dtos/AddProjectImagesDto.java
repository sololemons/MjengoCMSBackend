package com.siteoperationsservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddProjectImagesDto {
    private String constructionId;
    private Double latitude;
    private Double longitude;
    private String description;
    private LocalDate imageDate;
    private MultipartFile imageFile;
}
