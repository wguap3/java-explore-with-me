package ru.practicum.categories.service;

import ru.practicum.categories.dto.CategoryDtoIn;
import ru.practicum.categories.dto.CategoryDtoOut;

import java.util.List;

public interface CategoryService {
    List<CategoryDtoOut> getCategories(Integer from, Integer size);

    CategoryDtoOut getCategory(Long catId);

    CategoryDtoOut addCategory(CategoryDtoIn categoryDtoIn);

    void deleteCategory(Long catId);

    CategoryDtoOut updateCategory(Long catId, CategoryDtoIn categoryDtoIn);
}
