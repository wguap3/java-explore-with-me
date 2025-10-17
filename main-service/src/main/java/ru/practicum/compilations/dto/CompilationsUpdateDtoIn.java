package ru.practicum.compilations.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CompilationsUpdateDtoIn {
    List<Long> events;
    Boolean pinned;
    @Size(max = 50)
    String title;
}
