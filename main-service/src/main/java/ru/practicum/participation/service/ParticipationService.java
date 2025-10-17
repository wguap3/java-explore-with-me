package ru.practicum.participation.service;



import ru.practicum.participation.dto.ParticipationDtoOut;
import ru.practicum.participation.dto.ParticipationUpdateDtoIn;
import ru.practicum.participation.dto.ParticipationUpdateDtoOut;

import java.util.List;

public interface ParticipationService {
    List<ParticipationDtoOut> getUserParticipation(Long userId, Long eventId);

    ParticipationUpdateDtoOut updateEventRequests(Long userId, Long eventId, ParticipationUpdateDtoIn participationUpdateDtoIn);

    List<ParticipationDtoOut> getParticipationForAnotherUser(Long userId);

    ParticipationDtoOut addParticipation(Long userId, Long eventId);

    ParticipationDtoOut cancelParticipation(Long userId, Long requestId);
}
