package ru.practicum.compilations.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private Set<Long> events;
    private Boolean pinned = false;
    @Size(max = 50, message = "Название подборки не может превышать 50 символов")
    @NotBlank(message = "Название подборки не может быть пустым")
    private String title;
}
