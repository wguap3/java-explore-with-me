package ru.practicum.event.service;


import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventDtoOut;
import ru.practicum.event.dto.EventShortDtoOut;
import ru.practicum.event.dto.EventUpdateDtoIn;

import java.util.List;

public interface EventService {
    EventDtoOut addEvent(Long userId, EventDtoIn eventDtoIn);

    List<EventShortDtoOut> getEvents(Long userId, Integer from, Integer size);

    EventDtoOut getFullEvent(Long userId, Long eventId);

    EventDtoOut updateEvent(Long userId, Long eventId, EventUpdateDtoIn eventDtoIn);

    List<EventShortDtoOut> getPublicEvent(String text, Long[] categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size);

    EventDtoOut getPublicEventById(Long eventId);

    List<EventDtoOut> getAdminEvent(Long[] users, String[] states, Long[] categories, String rangeStart, String rangeEnd, Integer from, Integer size);

    EventDtoOut updateAdminEvent(Long eventId, EventUpdateDtoIn eventDtoIn);

    List<EventShortDtoOut> getCompilationsEvents(List<Long> eventIds);
}