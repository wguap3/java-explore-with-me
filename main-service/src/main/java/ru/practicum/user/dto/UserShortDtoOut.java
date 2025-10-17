package ru.practicum.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
public class UserShortDtoOut {
    @NotBlank
    Long id;
    @NotBlank
    String name;
}
