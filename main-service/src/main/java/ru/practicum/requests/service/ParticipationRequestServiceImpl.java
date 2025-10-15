package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventStatus;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapper.ParticipationRequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.ParticipationRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository repository;
    private final EventRepository eventRepository;// чтобы не было циклической зависимости
    private final UserService userService;
    private final ParticipationRequestMapper participationRequestMapper;

    @Override
    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {
        userService.findByIdOrThrow(userId);
        return repository.findAllByRequesterId(userId).stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userService.findByIdOrThrow(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request participation in own event");
        }

        if (!EventStatus.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("Cannot request participation in unpublished event");
        }

        if (repository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Request already exists for this user and event");
        }

        int confirmedCount = repository.findAllByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED).size();

        if (event.getParticipantLimit() != null && event.getParticipantLimit() > 0
                && confirmedCount >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit reached for this event");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus(event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED);

        return participationRequestMapper.toParticipationRequestDto(repository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = repository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        request.setStatus(RequestStatus.CANCELED);
        return participationRequestMapper.toParticipationRequestDto(repository.save(request));
    }

    @Override
    public ParticipationRequest findByIdOrThrow(Long reqId) {
        return repository.findById(reqId)
                .orElseThrow(() -> new NotFoundException("Request with id " + reqId + " not found"));
    }


    @Override
    public List<ParticipationRequestDto> getRequestsByEvent(Long eventId) {
        return repository.findAllByEventId(eventId).stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateRequestStatus(Long userId, Long eventId, Long requestId, RequestStatus status) {
        ParticipationRequest request = repository.findByIdAndEventId(requestId, eventId)
                .orElseThrow(() -> new NotFoundException("Request not found"));


        if (request.getStatus() == RequestStatus.CANCELED) return;

        request.setStatus(status);
        repository.save(request);
    }

}
