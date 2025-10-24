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
    private String annotation;
    private CategoryDtoOut category;
    private Long confirmedRequests;
    @NotBlank
    private String eventDate;
    private Long id;
    private UserShortDtoOut initiator;
    @NotNull
    private Boolean paid;
    private String title;
    private Long views;

}
