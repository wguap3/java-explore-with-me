package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
