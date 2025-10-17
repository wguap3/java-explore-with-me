package ru.practicum.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class CompilationsDtoIn {
    List<Integer> events;
    Boolean pinned;
    @NotBlank
    @Length(max = 50, min = 0)
    String title;
}
