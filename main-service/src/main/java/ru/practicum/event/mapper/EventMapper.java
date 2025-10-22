package ru.practicum.event.mapper;


import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventDtoOut;
import ru.practicum.event.dto.EventShortDtoOut;
import ru.practicum.event.dto.EventUpdateDtoIn;
import ru.practicum.event.model.Event;
import ru.practicum.participation.repository.ParticipationRepository;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;

import static ru.practicum.constants.DateTimeFormatConstants.FORMATTER;


@Mapper(componentModel = "spring")
public abstract class EventMapper {


    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ParticipationRepository participationRepository;

    @Named("stringToLocalDateTime")
    protected LocalDateTime stringToLocalDateTime(String date) {
        return date != null ? LocalDateTime.parse(date, FORMATTER) : null;
    }

    @Named("localDateTimeToString")
    protected String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(FORMATTER) : null;
    }

    @Mapping(target = "category", source = "eventDtoIn.category")
    @Mapping(target = "annotation", source = "eventDtoIn.annotation")
    @Mapping(target = "description", source = "eventDtoIn.description")
    @Mapping(target = "eventDate", source = "eventDtoIn.eventDate", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "locationLat", source = "eventDtoIn.location.lat")
    @Mapping(target = "locationLon", source = "eventDtoIn.location.lon")
    @Mapping(target = "paid", expression = "java(eventDtoIn.getPaid() == null ? false : eventDtoIn.getPaid())")
    @Mapping(target = "participantLimit", expression = "java(eventDtoIn.getParticipantLimit() == null ? 0 : eventDtoIn.getParticipantLimit())")
    @Mapping(target = "requestModeration", expression = "java(eventDtoIn.getRequestModeration() == null || eventDtoIn.getRequestModeration())")
    @Mapping(target = "state", expression = "java(ru.practicum.enums.EveState.PENDING)")
    @Mapping(target = "title", source = "eventDtoIn.title")
    public abstract Event mapEventDtoInToEvent(EventDtoIn eventDtoIn);

    @Mapping(target = "annotation", source = "event.annotation")
    @Mapping(target = "category", expression = "java(categoryService.getCategory(event.getCategory()))")
    @Mapping(target = "confirmedRequests", expression = "java(participationRepository.countByEventIdAndConfirmed(event.getId()))")
    @Mapping(target = "createdOn", source = "event.createdOn", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "description", source = "event.description")
    @Mapping(target = "eventDate", source = "event.eventDate", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "initiator", expression = "java(userService.getUser(event.getInitiator()))")
    @Mapping(target = "location", expression = "java(new Location(event.getLocationLat(), event.getLocationLon()))")
    @Mapping(target = "paid", source = "event.paid")
    @Mapping(target = "participantLimit", source = "event.participantLimit")
    @Mapping(target = "publishedOn", source = "event.publishedOn", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "requestModeration", source = "event.requestModeration")
    @Mapping(target = "state", expression = "java(event.getState().toString())")
    @Mapping(target = "title", source = "event.title")
    public abstract EventDtoOut mapEventToEventDtoOut(Event event);

    @Mapping(target = "annotation", source = "event.annotation")
    @Mapping(target = "category", expression = "java(categoryService.getCategory(event.getCategory()))")
    @Mapping(target = "confirmedRequests", expression = "java(participationRepository.countByEventIdAndConfirmed(event.getId()))")
    @Mapping(target = "eventDate", source = "event.eventDate", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "initiator", expression = "java(userService.getUser(event.getInitiator()))")
    @Mapping(target = "paid", source = "event.paid")
    @Mapping(target = "title", source = "event.title")
    @Mapping(target = "views", expression = "java(views != null ? views : 0L)")
    public abstract EventShortDtoOut mapEventToEventShortDtoOut(Event event, Long views);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(
            target = "eventDate",
            expression = "java(dto.getEventDate() != null ? java.time.LocalDateTime.parse(dto.getEventDate(), ru.practicum.constants.DateTimeFormatConstants.FORMATTER) : event.getEventDate())"
    )
    @Mapping(target = "locationLat", expression = "java(dto.getLocation() != null ? dto.getLocation().getLat() : event.getLocationLat())")
    @Mapping(target = "locationLon", expression = "java(dto.getLocation() != null ? dto.getLocation().getLon() : event.getLocationLon())")
    public abstract void updateEventFromDto(@MappingTarget Event event, EventUpdateDtoIn dto);
}


