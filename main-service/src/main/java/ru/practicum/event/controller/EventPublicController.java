package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDtoOut;
import ru.practicum.event.dto.EventShortDtoOut;
import ru.practicum.event.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {
    private final EventService eventService;
    private final StatClient statClient;

    @GetMapping
    public List<EventShortDtoOut> getPublicEvent(@RequestParam(name = "text", defaultValue = "_") String text,
                                                 @RequestParam(name = "categories", required = false) Long[] categories,
                                                 @RequestParam(name = "paid", required = false) Boolean paid,
                                                 @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                 @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                 @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                                 @RequestParam(name = "sort", required = false) String sort,
                                                 @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") Integer size, HttpServletRequest request) {
        log.info("GET/ Проверка параметров запроса метода getPublicEvent, text - {}, categories - {}, paid - {}, rangeStart - {}" +
                ", rangeEnd - {}, onlyAvailable - {}, sort - {}, from - {}, size - {}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.getPublicEvent(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventDtoOut getPublicEventById(@PathVariable(name = "id") Long eventId, HttpServletRequest request) {
        log.info("GET/ Проверка параметров запроса метода getPublicEventById, id - {}", eventId);
        return eventService.getPublicEventById(eventId, request);
    }

}
