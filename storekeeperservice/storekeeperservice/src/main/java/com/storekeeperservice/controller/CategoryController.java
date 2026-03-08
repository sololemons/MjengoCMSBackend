package com.storekeeperservice.controller;

import com.storekeeperservice.dtos.CreateCategoryDto;
import com.storekeeperservice.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    @PostMapping("/create/category")
    public ResponseEntity<String> createCategory(@RequestBody CreateCategoryDto createCategoryDto) {
        return ResponseEntity.ok(categoryService.createCategory(createCategoryDto));
    }
    @PutMapping("/update/category")
    public ResponseEntity<String> updateCategory(@RequestParam long categoryId, @RequestBody CreateCategoryDto createCategoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId,createCategoryDto));
    }
    @GetMapping("/get/categories")
    public ResponseEntity<List<CreateCategoryDto>> getCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    @DeleteMapping("/delete/category")
    public ResponseEntity<String> deleteCategory(@RequestParam long categoryId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }
}
