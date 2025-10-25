package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDtoOut;
import ru.practicum.event.dto.EventUpdateDtoIn;
import ru.practicum.event.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventDtoOut> getAdminEvent(@RequestParam(name = "users", required = false) Long[] users,
                                           @RequestParam(name = "states", required = false) String[] states,
                                           @RequestParam(name = "categories", required = false) Long[] categories,
                                           @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                           @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET/ Проверка параметров запроса метода getAdminEvent, users - {}, states - {}, categories - {}, rangeStart - {}" +
                ", rangeEnd - {}, from - {}, size - {}", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getAdminEvent(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventDtoOut updateAdminEvent(@PathVariable(name = "eventId") Long eventId,
                                        @Valid @RequestBody EventUpdateDtoIn eventDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода updateAdminEvent, eventId - {}, eventDtoIn - {}", eventId, eventDtoIn);
        return eventService.updateAdminEvent(eventId, eventDtoIn);
    }

}
