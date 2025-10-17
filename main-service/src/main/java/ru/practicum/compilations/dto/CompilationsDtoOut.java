package ru.practicum.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.event.dto.EventShortDtoOut;

import java.util.List;

@Data
public class CompilationsDtoOut {
    List<EventShortDtoOut> events;
    @NotNull
    Long id;
    @NotNull
    Boolean pinned;
    @NotBlank
    @Length(max = 50, min = 0)
    String title;
}
