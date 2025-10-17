package ru.practicum.compilations.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class CompilationsUpdateDtoIn {
    List<Integer> events;
    Boolean pinned;
    @Length(max = 50, min = 0)
    String title;
}
