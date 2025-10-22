package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryDtoIn;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    public List<CategoryDtoOut> getCategories(Integer from, Integer size) {
        return categoryRepository.findAll().stream().map(categoryMapper::mapCategoryToCategoryDtoOut).toList();
    }

    @Override
    public CategoryDtoOut getCategory(Long catId) {
        return categoryMapper.mapCategoryToCategoryDtoOut(categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found")));
    }

    @Transactional
    @Override
    public CategoryDtoOut addCategory(CategoryDtoIn categoryDtoIn) {
        validCat(categoryDtoIn.getName());
        return categoryMapper.mapCategoryToCategoryDtoOut(categoryRepository.save(categoryMapper.mapCategoryDtoInToCategory(categoryDtoIn)));
    }

    @Transactional
    @Override
    public void deleteCategory(Long catId) {
        getCategory(catId);
        if (!eventRepository.findAllByCategory(catId).isEmpty()) {
            throw new ConflictException(("C категорий не должно быть связано ни одного события!"));
        }
        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDtoOut updateCategory(Long catId, CategoryDtoIn categoryDtoIn) {
        Category categoryById = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        if (!categoryById.getName().equals(categoryDtoIn.getName())) {
            validCat(categoryDtoIn.getName());
        }
        categoryById.setName(categoryDtoIn.getName());
        return categoryMapper.mapCategoryToCategoryDtoOut(categoryRepository.save(categoryById));
    }

    public void validCat(String name) {
        if (categoryRepository.findByName(name) != null) {
            throw new ConflictException("Имя категории должно быть уникальным!");
        }
    }

}
