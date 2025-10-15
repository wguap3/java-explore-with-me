package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Optional<Category> existing = categoryRepository.findByName(categoryDto.getName());
        if (existing.isPresent()) {
            throw new ConflictException("Категория с таким именем уже существует");
        }

        Category category = categoryMapper.toCategory(categoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }


    @Override
    public CategoryDto update(Long categoryId, CategoryDto categoryDto) {
        Category existingCategory = findByIdOrThrow(categoryId);

        Optional<Category> categoryWithSameName = categoryRepository.findByName(categoryDto.getName());
        if (categoryWithSameName.isPresent() && !categoryWithSameName.get().getId().equals(categoryId)) {
            throw new ConflictException("Категория с таким именем уже существует");
        }

        categoryMapper.updateCategoryFromDto(categoryDto, existingCategory);
        return categoryMapper.toCategoryDto(categoryRepository.save(existingCategory));
    }


    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = findByIdOrThrow(categoryId);
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<Category> categories = categoryRepository.findAll(pageable).getContent();

        return categories.stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteCategory(Long categoryId) {
        boolean hasEvents = categoryRepository.existsByCategoryId(categoryId);
        if (hasEvents) {
            throw new ConflictException("Невозможно удалить категорию: к ней привязаны события");
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public Category findByIdOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id " + categoryId + " not found"));
    }
}
