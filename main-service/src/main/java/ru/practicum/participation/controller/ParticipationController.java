package ru.practicum.participation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.participation.dto.ParticipationDtoOut;
import ru.practicum.participation.service.ParticipationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class ParticipationController {
    private final ParticipationService participationService;

    @GetMapping
    public List<ParticipationDtoOut> getParticipationForAnotherUser(@PathVariable(name = "userId") Long userId) {
        log.info("GET/ Проверка параметров запроса метода getParticipationForAnotherUser, userId - {}", userId);
        return participationService.getParticipationForAnotherUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationDtoOut addParticipation(@PathVariable(name = "userId") Long userId,
                                                @RequestParam(name = "eventId") Long eventId) {
        log.info("POST/ Проверка параметров запроса метода addParticipation, userId - {}, eventId - {}", userId, eventId);
        return participationService.addParticipation(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationDtoOut cancelParticipation(@PathVariable(name = "userId") Long userId,
                                                   @PathVariable(name = "requestId") Long requestId) {
        log.info("PATCH/ Проверка параметров запроса метода cancelParticipation, userId - {}, requestId - {}", userId, requestId);
        return participationService.cancelParticipation(userId, requestId);
    }

}
