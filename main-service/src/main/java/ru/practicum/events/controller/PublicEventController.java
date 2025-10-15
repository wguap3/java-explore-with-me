package ru.practicum.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.service.EventService;
import ru.practicum.events.service.EventStatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;
    private final EventStatsService eventStatsService;

    @GetMapping("/events")
    public List<EventShortDto> getPublicEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();

        eventStatsService.registerView(uri, ip);

        return eventService.getPublicEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size
        );
    }


    @GetMapping("/events/{id}")
    public EventFullDto getPublicEventById(
            @PathVariable Long id,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        eventStatsService.registerView(uri, ip);

        return eventService.getPublicEventById(id);
    }
}

