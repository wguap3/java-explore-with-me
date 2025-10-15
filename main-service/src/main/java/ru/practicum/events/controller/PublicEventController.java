package ru.practicum.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getPublicEvents(
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String sort, // "views" или "date"
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return eventService.getPublicEvents(category, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getPublicEventById(
            @PathVariable Long id,
            HttpServletRequest request) {

        EventFullDto event = eventService.getPublicEventById(id);

        // Логируем просмотр
        String clientIp = request.getRemoteAddr();
        eventService.addView(event.getId(), clientIp);

        return event;
    }
}

