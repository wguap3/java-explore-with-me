package ru.practicum.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserShortDtoOut {
    @NotBlank
    Long id;
    @NotBlank
    String name;
}
