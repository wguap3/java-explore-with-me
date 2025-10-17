package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventDtoOut;
import ru.practicum.event.dto.EventShortDtoOut;
import ru.practicum.event.dto.EventUpdateDtoIn;
import ru.practicum.event.service.EventService;
import ru.practicum.participation.dto.ParticipationDtoOut;
import ru.practicum.participation.dto.ParticipationUpdateDtoIn;
import ru.practicum.participation.dto.ParticipationUpdateDtoOut;
import ru.practicum.participation.service.ParticipationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;
    private final ParticipationService participationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtoOut addEvent(@PathVariable(name = "userId") Long userId,
                                @Valid @RequestBody EventDtoIn eventDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addEvent, userId - {}, eventDtoIn - {}", userId, eventDtoIn);
        return eventService.addEvent(userId, eventDtoIn);
    }

    @GetMapping
    public List<EventShortDtoOut> getEvents(@PathVariable(name = "userId") Long userId,
                                            @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET/ Проверка параметров запроса метода getEvent, userId - {}, from - {}, size - {}", userId, from, size);
        return eventService.getEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDtoOut getFullEvent(@PathVariable(name = "userId") Long userId,
                                    @PathVariable(name = "eventId") Long eventId) {
        log.info("GET/ Проверка параметров запроса метода getFullEvent, userId - {}, eventId - {}", userId, eventId);
        return eventService.getFullEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDtoOut updateEvent(@PathVariable(name = "userId") Long userId,
                                   @PathVariable(name = "eventId") Long eventId,
                                   @Valid @RequestBody EventUpdateDtoIn eventDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода updateEvent, userId - {}, eventId - {}, eventDtoIn - {}", userId, eventId, eventDtoIn);
        return eventService.updateEvent(userId, eventId, eventDtoIn);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationDtoOut> getEventRequests(@PathVariable(name = "userId") Long userId,
                                                      @PathVariable(name = "eventId") Long eventId) {
        log.info("GET/ Проверка параметров запроса метода getEventRequests, userId - {}, eventId - {}", userId, eventId);
        return participationService.getUserParticipation(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public ParticipationUpdateDtoOut updateEventRequests(@PathVariable(name = "userId") Long userId,
                                                         @PathVariable(name = "eventId") Long eventId,
                                                         @RequestBody (required = false) ParticipationUpdateDtoIn participationUpdateDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода updateEventRequests, userId - {}, eventId - {}, participationUpdateDtoIn - {}", userId, eventId, participationUpdateDtoIn);
        return participationService.updateEventRequests(userId, eventId, participationUpdateDtoIn);
    }
}
