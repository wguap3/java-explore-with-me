package ru.practicum.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.location.Location;
import ru.practicum.user.dto.UserShortDtoOut;

@Data
public class EventDtoOut {
    @NotBlank
    String annotation;
    @NotNull
    CategoryDtoOut category;
    Long confirmedRequests;
    String createdOn;
    String description;
    @NotBlank
    String eventDate;
    Long id;
    @NotNull
    UserShortDtoOut initiator;
    @NotNull
    Location location;
    @NotNull
    Boolean paid;
    Integer participantLimit;
    String publishedOn;
    Boolean requestModeration;
    String state;
    @NotBlank
    String title;
    Integer views;
}
