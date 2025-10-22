package ru.practicum.categories.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDtoIn;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.categories.service.CategoryService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDtoOut addCategory(@Valid @RequestBody CategoryDtoIn categoryDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addCategory, categoryDtoIn - {}", categoryDtoIn.getName());
        return categoryService.addCategory(categoryDtoIn);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(name = "catId") Long catId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteCategory, catId - {}", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDtoOut updateCategory(@PathVariable(name = "catId") Long catId,
                                         @Valid @RequestBody CategoryDtoIn categoryDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода updateCategory, catId - {}, categoryDtoIn - {}", catId, categoryDtoIn);
        return categoryService.updateCategory(catId, categoryDtoIn);
    }
}

