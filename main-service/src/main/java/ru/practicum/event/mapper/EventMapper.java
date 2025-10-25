package ru.practicum.event.mapper;


import org.mapstruct.*;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventDtoOut;
import ru.practicum.event.dto.EventShortDtoOut;
import ru.practicum.event.dto.EventUpdateDtoIn;
import ru.practicum.event.model.Event;
import ru.practicum.user.dto.UserShortDtoOut;

import java.time.LocalDateTime;

import static ru.practicum.constants.DateTimeFormatConstants.FORMATTER;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String date) {
        return date != null ? LocalDateTime.parse(date, FORMATTER) : null;
    }

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(FORMATTER) : null;
    }

    @Mapping(target = "category", source = "category")
    @Mapping(target = "annotation", source = "annotation")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "eventDate", source = "eventDate", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "locationLat", source = "location.lat")
    @Mapping(target = "locationLon", source = "location.lon")
    @Mapping(target = "paid", expression = "java(eventDtoIn.getPaid() != null ? eventDtoIn.getPaid() : false)")
    @Mapping(target = "participantLimit", expression = "java(eventDtoIn.getParticipantLimit() != null ? eventDtoIn.getParticipantLimit() : 0)")
    @Mapping(target = "requestModeration", expression = "java(eventDtoIn.getRequestModeration() == null || eventDtoIn.getRequestModeration())")
    @Mapping(target = "state", expression = "java(ru.practicum.enums.EveState.PENDING)")
    @Mapping(target = "title", source = "title")
    Event mapEventDtoInToEvent(EventDtoIn eventDtoIn);

    @Mapping(target = "annotation", source = "event.annotation")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "createdOn", source = "event.createdOn", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "description", source = "event.description")
    @Mapping(target = "eventDate", source = "event.eventDate", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "location", expression = "java(new Location(event.getLocationLat(), event.getLocationLon()))")
    @Mapping(target = "paid", source = "event.paid")
    @Mapping(target = "participantLimit", source = "event.participantLimit")
    @Mapping(target = "publishedOn", source = "event.publishedOn", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "requestModeration", source = "event.requestModeration")
    @Mapping(target = "state", expression = "java(event.getState().toString())")
    @Mapping(target = "title", source = "event.title")
    EventDtoOut mapEventToEventDtoOut(
            Event event,
            CategoryDtoOut category,
            UserShortDtoOut initiator,
            Long confirmedRequests
    );

    @Mapping(target = "annotation", source = "event.annotation")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "eventDate", source = "event.eventDate", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "paid", source = "event.paid")
    @Mapping(target = "title", source = "event.title")
    @Mapping(target = "views", source = "views")
    EventShortDtoOut mapEventToEventShortDtoOut(
            Event event,
            CategoryDtoOut category,
            UserShortDtoOut initiator,
            Long confirmedRequests,
            Long views
    );

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(
            target = "eventDate",
            expression = "java(dto.getEventDate() != null ? java.time.LocalDateTime.parse(dto.getEventDate(), ru.practicum.constants.DateTimeFormatConstants.FORMATTER) : event.getEventDate())"
    )
    @Mapping(target = "locationLat", expression = "java(dto.getLocation() != null ? dto.getLocation().getLat() : event.getLocationLat())")
    @Mapping(target = "locationLon", expression = "java(dto.getLocation() != null ? dto.getLocation().getLon() : event.getLocationLon())")
    void updateEventFromDto(@MappingTarget Event event, EventUpdateDtoIn dto);
}


