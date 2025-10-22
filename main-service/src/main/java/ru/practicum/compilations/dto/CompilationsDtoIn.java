package ru.practicum.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompilationsDtoIn {
    List<Long> events;
    Boolean pinned;
    @NotBlank
    @Size(max = 50)
    String title;
}
