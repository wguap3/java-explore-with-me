package ru.practicum.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.enums.EveState;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventDtoOut;
import ru.practicum.event.dto.EventShortDtoOut;
import ru.practicum.event.model.Event;
import ru.practicum.location.Location;
import ru.practicum.participation.repository.ParticipationRepository;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final CategoryService categoryService;
    private final UserService userService;
    private final ParticipationRepository participationRepository;
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Event mapEventDtoInToEvent(EventDtoIn eventDtoIn) {
        Event event = new Event();
        event.setAnnotation(eventDtoIn.getAnnotation());
        event.setCategory(eventDtoIn.getCategory());
        event.setDescription(eventDtoIn.getDescription());
        event.setEventDate(LocalDateTime.parse(eventDtoIn.getEventDate(), format));
        event.setLocationLat(eventDtoIn.getLocation().getLat());
        event.setLocationLon(eventDtoIn.getLocation().getLon());
        if (eventDtoIn.getPaid() == null) {
            event.setPaid(false);
        } else {
            event.setPaid(eventDtoIn.getPaid());
        }
        if (eventDtoIn.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        } else {
            event.setParticipantLimit(eventDtoIn.getParticipantLimit());
        }

        if (eventDtoIn.getRequestModeration() != null && !eventDtoIn.getRequestModeration()) {
            event.setRequestModeration(false);
        } else {
            event.setRequestModeration(true);
        }
        event.setState(EveState.PENDING);
        event.setTitle(eventDtoIn.getTitle());
        return event;
    }

    public EventDtoOut mapEventToEventDtoOut(Event event) {
        EventDtoOut eventDtoOut = new EventDtoOut();
        eventDtoOut.setAnnotation(event.getAnnotation());
        eventDtoOut.setCategory(categoryService.getCategory(event.getCategory()));
        eventDtoOut.setConfirmedRequests(participationRepository.countByEventIdAndConfirmed(event.getId()));
        eventDtoOut.setCreatedOn(event.getCreatedOn().format(format));
        eventDtoOut.setDescription(event.getDescription());
        eventDtoOut.setEventDate(event.getEventDate().format(format));
        eventDtoOut.setId(event.getId());
        eventDtoOut.setInitiator(userService.getUser(event.getInitiator()));
        Location location = new Location();
        location.setLat(event.getLocationLat());
        location.setLon(event.getLocationLon());
        eventDtoOut.setLocation(location);
        eventDtoOut.setPaid(event.getPaid());
        eventDtoOut.setParticipantLimit(event.getParticipantLimit());
        if (event.getPublishedOn() != null) {
            eventDtoOut.setPublishedOn(event.getPublishedOn().format(format));
        }
        eventDtoOut.setRequestModeration(event.getRequestModeration());
        eventDtoOut.setState(event.getState().toString());
        eventDtoOut.setTitle(event.getTitle());
//        eventDtoOut.setViews(eventClient.getHits(event.getCreatedOn().format(format), LocalDateTime.now().format(format), new String[]{"/events/" + event.getId()}, true).getFirst().getHits());
        return eventDtoOut;
    }

    public EventShortDtoOut mapEventToEventShortDtoOut(Event event) {
        EventShortDtoOut eventShortDtoOut = new EventShortDtoOut();
        eventShortDtoOut.setAnnotation(event.getAnnotation());
        eventShortDtoOut.setCategory(categoryService.getCategory(event.getCategory()));
        eventShortDtoOut.setConfirmedRequests(participationRepository.countByEventIdAndConfirmed(event.getId()));
        eventShortDtoOut.setEventDate(event.getEventDate().format(format));
        eventShortDtoOut.setId(event.getId());
        eventShortDtoOut.setInitiator(userService.getUser(event.getInitiator()));
        eventShortDtoOut.setPaid(event.getPaid());
        eventShortDtoOut.setTitle(event.getTitle());
//        eventShortDtoOut.setViews(eventClient.getHits(event.getCreatedOn().format(format), LocalDateTime.now().format(format), new String[]{"/events/" + event.getId()}, true).getFirst().getHits());
        return eventShortDtoOut;
    }

}