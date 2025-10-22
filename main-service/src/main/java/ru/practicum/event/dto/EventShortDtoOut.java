package ru.practicum.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.user.dto.UserShortDtoOut;


@Getter
@Setter
public class EventShortDtoOut {
    String annotation;
    CategoryDtoOut category;
    Long confirmedRequests;
    @NotBlank
    String eventDate;
    Long id;
    UserShortDtoOut initiator;
    @NotNull
    Boolean paid;
    String title;
    Long views;

}
