package ru.practicum.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.SortType;
import ru.practicum.events.service.EventService;
import ru.practicum.events.service.EventStatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;
    private final EventStatsService eventStatsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findEvents(@RequestParam(required = false) String text,
                                          @RequestParam(required = false) List<Integer> categories,
                                          @RequestParam(required = false) Boolean paid,
                                          @RequestParam(required = false) String rangeStart,
                                          @RequestParam(required = false) String rangeEnd,
                                          @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                          @RequestParam(required = false) SortType sort,
                                          @RequestParam(required = false, defaultValue = "0") int from,
                                          @RequestParam(required = false, defaultValue = "10") int size,
                                          HttpServletRequest request) {
        List<EventShortDto> events = eventService.findEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);

        return events;
    }


    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findEventById(@PathVariable long id, HttpServletRequest request) {

        EventFullDto event = eventService.findEventById(id, request);

        return event;
    }
}

