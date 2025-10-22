package ru.practicum.event.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.practicum.location.Location;

@Getter
@Setter
public class EventDtoIn {
    @NotBlank
    @Length(min = 20, max = 2000)
    String annotation;
    @NotNull
    Long category;
    @NotBlank
    @Length(min = 20, max = 7000)
    String description;
    @NotBlank
    String eventDate;
    @Valid
    Location location;
    Boolean paid;
    @PositiveOrZero
    Integer participantLimit;
    Boolean requestModeration;
    String stateAction;
    @NotBlank
    @Length(min = 3, max = 120)
    String title;
}
