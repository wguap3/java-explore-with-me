package ru.practicum.categories.mapper;

import org.mapstruct.Mapper;
import ru.practicum.categories.dto.CategoryDtoIn;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.categories.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDtoOut mapCategoryToCategoryDtoOut(Category category);

    Category mapCategoryDtoInToCategory(CategoryDtoIn categoryDtoIn);
}
