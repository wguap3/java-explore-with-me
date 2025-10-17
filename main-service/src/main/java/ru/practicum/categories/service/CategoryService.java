package ru.practicum.categories.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.categories.dto.CategoryDtoIn;
import ru.practicum.categories.dto.CategoryDtoOut;

import java.util.List;

public interface CategoryService {
    List<CategoryDtoOut> getCategories(Integer from, Integer size);

    CategoryDtoOut getCategory(Long catId);

    CategoryDtoOut addCategory(CategoryDtoIn categoryDtoIn);

    ResponseEntity<Void> deleteCategory(Long catId);

    CategoryDtoOut updateCategory(Long catId, CategoryDtoIn categoryDtoIn);
}
