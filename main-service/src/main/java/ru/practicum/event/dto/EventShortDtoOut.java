package ru.practicum.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.user.dto.UserShortDtoOut;


@Data
public class EventShortDtoOut {
    @NotBlank
    String annotation;
    @NotNull
    CategoryDtoOut category;
    Long confirmedRequests;
    @NotBlank
    String eventDate;
    Long id;
    @NotNull
    UserShortDtoOut initiator;
    @NotNull
    Boolean paid;
    @NotBlank
    String title;
    Integer views;

}
