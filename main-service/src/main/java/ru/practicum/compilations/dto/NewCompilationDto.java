package ru.practicum.compilations.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    @NotBlank(message = "Название подборки не может быть пустым")
    @Size(max = 50, message = "Название подборки не может превышать 50 символов")
    private Set<Long> events;
    private Boolean pinned = false;
    private String title;
}
