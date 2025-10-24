package ru.practicum.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.location.Location;
import ru.practicum.user.dto.UserShortDtoOut;

@Getter
@Setter
public class EventDtoOut {
    @NotBlank
    private String annotation;
    @NotNull
    private CategoryDtoOut category;
    private Long confirmedRequests;
    private String createdOn;
    private String description;
    @NotBlank
    private String eventDate;
    private Long id;
    @NotNull
    private UserShortDtoOut initiator;
    @NotNull
    private Location location;
    @NotNull
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private String state;
    @NotBlank
    private String title;
    private Long views;
}
