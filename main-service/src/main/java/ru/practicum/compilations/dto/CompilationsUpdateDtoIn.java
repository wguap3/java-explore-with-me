package ru.practicum.compilations.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompilationsUpdateDtoIn {
    List<Long> events;
    Boolean pinned;
    @Size(max = 50)
    String title;
}
