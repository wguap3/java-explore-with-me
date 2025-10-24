package ru.practicum.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.dto.EventShortDtoOut;

import java.util.List;

@Getter
@Setter
public class CompilationsDtoOut {
    private List<EventShortDtoOut> events;
    @NotNull
    private Long id;
    @NotNull
    private Boolean pinned;
    @NotBlank
    @Size(max = 50)
    private String title;
}
