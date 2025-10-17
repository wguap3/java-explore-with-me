package ru.practicum.categories.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDtoOut {
    Long id;
    @NotBlank
    String name;
}
