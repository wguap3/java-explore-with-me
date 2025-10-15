package ru.practicum.events.service;

import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    EventResponseDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsByUser(Long userId, int from, int size);

    EventFullDto getEventByUser(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest request);

    Event findByIdOrThrow(Long userId);

    List<EventFullDto> getAdminEvents(
            List<Long> userIds,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            int from,
            int size
    );

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest request);


    EventFullDto getPublicEventById(Long eventId);

    List<EventShortDto> getPublicEvents(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        String rangeStart,
                                        String rangeEnd,
                                        Boolean onlyAvailable,
                                        String sort,
                                        int from,
                                        int size);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestStatus(Long userId,
                                                            Long eventId,
                                                            EventRequestStatusUpdateRequest request);


}
