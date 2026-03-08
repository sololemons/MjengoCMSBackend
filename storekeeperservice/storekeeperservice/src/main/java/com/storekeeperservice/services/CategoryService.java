package com.storekeeperservice.services;

import com.storekeeperservice.dtos.CreateCategoryDto;
import com.storekeeperservice.entities.Category;
import com.storekeeperservice.exceptions.UserNotFoundException;
import com.storekeeperservice.repositories.CategoryRepository;
import com.storekeeperservice.utilis.MapperDtos;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final MapperDtos mapperDtos;
    public String createCategory(CreateCategoryDto createCategoryDto) {
        Optional<Category> category = categoryRepository.findByCategoryName(createCategoryDto.getCategoryName());

        if (category.isPresent()) {
            throw new IllegalArgumentException("Category already exists");
        }

        Category newCategory = new Category();
        newCategory.setCategoryName(createCategoryDto.getCategoryName());
        newCategory.setDescription(createCategoryDto.getCategoryDescription());

        categoryRepository.save(newCategory);

        return "New category created: " + newCategory.getCategoryName();
    }
    public @Nullable String updateCategory(long categoryId, CreateCategoryDto createCategoryDto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new UserNotFoundException("Category not found"));
        category.setCategoryName(createCategoryDto.getCategoryName());
        category.setDescription(createCategoryDto.getCategoryDescription());
        categoryRepository.save(category);
        return "Category updated from : " + category.getCategoryName() + " "+ "to" + createCategoryDto.getCategoryName();

    }

    public @Nullable List<CreateCategoryDto> getAllCategories() {
        return mapperDtos.mapCategoryListToDtoList(categoryRepository.findAll());
    }

    public @Nullable String deleteCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new UserNotFoundException("Category not found"));
        categoryRepository.delete(category);
        return "Category deleted: " + category.getCategoryName();
    }
}
