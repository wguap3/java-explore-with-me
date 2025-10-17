package ru.practicum.categories.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
public class CategoryDtoIn {
    @NotBlank
    String name;
}
