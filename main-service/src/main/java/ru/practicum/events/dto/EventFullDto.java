package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.events.model.EventStatus;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {

    private Long id;
    private String title;
    private String annotation;
    private String description;

    private CategoryDto category;
    private UserShortDto initiator;
    private LocationDto location;

    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;

    private LocalDateTime eventDate;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;

    private EventStatus state;

    private Long confirmedRequests;
    private Long views;
}

