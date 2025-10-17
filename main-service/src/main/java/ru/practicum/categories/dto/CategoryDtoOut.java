package ru.practicum.categories.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Data
public class CategoryDtoOut {
    Long id;
    @NotBlank
    String name;
}
