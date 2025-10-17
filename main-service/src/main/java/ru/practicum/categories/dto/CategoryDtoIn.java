package ru.practicum.categories.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDtoIn {
    @NotBlank
    String name;
}
