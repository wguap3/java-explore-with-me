package ru.practicum.requests.service;

import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.RequestStatus;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestDto> getRequestsByUser(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    ParticipationRequest findByIdOrThrow(Long reqId);

    List<ParticipationRequestDto> getRequestsByEvent(Long eventId);

    void updateRequestStatus(Long userId, Long eventId, Long requestId, RequestStatus status);


}
