package ru.practicum.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CompilationsDtoIn {
    List<Long> events;
    Boolean pinned;
    @NotBlank
    @Size(max = 50)
    String title;
}
