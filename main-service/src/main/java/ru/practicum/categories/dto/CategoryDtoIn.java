package ru.practicum.categories.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDtoIn {
    @NotBlank
    private String name;
}
