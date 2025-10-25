package ru.practicum.categories.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CategoryDtoOut {
    private Long id;
    @NotBlank
    private String name;
}
