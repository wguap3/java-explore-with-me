package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.events.dto.*;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventStatus;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapper.ParticipationRequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.service.ParticipationRequestService;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventStatsService eventStatsService;
    private final ParticipationRequestService requestService;
    private final ParticipationRequestMapper participationRequestMapper;

    @Override
    public NewEventDto addEvent(Long userId, NewEventDto newEventDto) {
        User initiator = userService.findByIdOrThrow(userId);
        Category category = categoryService.findByIdOrThrow(newEventDto.getCategory());

        Event event = eventMapper.toEvent(newEventDto);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setState(EventStatus.PENDING);
        event.setCreatedOn(LocalDateTime.now());

        return eventMapper.toNewEventDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, page).stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        Event event = findByIdOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("User is not the initiator of the event");
        }
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = findByIdOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("User is not the initiator of the event");
        }

        if (request.getCategory() != null) {
            event.setCategory(categoryService.findByIdOrThrow(request.getCategory()));
        }

        if (request.getLocation() != null) {
            eventMapper.updateEventLocationFromDto(request.getLocation(), event);
        }

        eventMapper.updateEventFromUser(request, event);

        if (request.getStateAction() != null) {
            switch (request.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventStatus.PENDING);
                case CANCEL_REVIEW -> event.setState(EventStatus.CANCELED);
            }
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> getAdminEvents(List<Long> userIds,
                                             List<String> states,
                                             List<Long> categories,
                                             String rangeStart,
                                             String rangeEnd,
                                             int from,
                                             int size) {
        List<EventStatus> statusList = (states != null) ?
                states.stream().map(EventStatus::valueOf).toList() : null;

        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, DateTimeFormatter.ISO_DATE_TIME) : null;
        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, DateTimeFormatter.ISO_DATE_TIME) : null;

        PageRequest page = PageRequest.of(from / size, size);

        return eventRepository.findAdminEvents(userIds, statusList, categories, start, end, page).stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest request) {
        Event event = findByIdOrThrow(eventId);

        if (request.getCategory() != null) {
            event.setCategory(categoryService.findByIdOrThrow(request.getCategory()));
        }

        if (request.getLocation() != null) {
            eventMapper.updateEventLocationFromDto(request.getLocation(), event);
        }

        eventMapper.updateEventFromAdmin(request, event);

        if (request.getStateAction() != null) {
            switch (request.getStateAction()) {
                case PUBLISH_EVENT -> {
                    event.setState(EventStatus.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> event.setState(EventStatus.CANCELED);
            }
        }
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public Event findByIdOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));
    }

    @Override
    public void addView(Long eventId, String ip) {
        eventStatsService.registerView(eventId, ip);
    }

    @Override
    public Map<Long, Long> getViews(List<Long> eventIds) {
        return eventStatsService.getViews(eventIds);
    }

    @Override
    public List<EventShortDto> getPublicEvents(Long categoryId, String sort, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);

        List<Event> events = (categoryId != null) ?
                eventRepository.findAllByStateAndCategoryId(EventStatus.PUBLISHED, categoryId, page).getContent() :
                eventRepository.findAllByState(EventStatus.PUBLISHED, page).getContent();

        List<EventShortDto> dtoList = events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());

        Map<Long, Long> views = eventStatsService.getViews(
                dtoList.stream().map(EventShortDto::getId).toList()
        );

        dtoList.forEach(e -> e.setViews(views.getOrDefault(e.getId(), 0L)));

        if ("views".equalsIgnoreCase(sort)) {
            dtoList.sort(Comparator.comparingLong(EventShortDto::getViews).reversed());
        } else {
            dtoList.sort(Comparator.comparing(EventShortDto::getEventDate));
        }

        return dtoList;
    }

    @Override
    public EventFullDto getPublicEventById(Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, EventStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        EventFullDto dto = eventMapper.toEventFullDto(event);
        Long views = eventStatsService.getViews(List.of(eventId)).getOrDefault(eventId, 0L);
        dto.setViews(views);

        return dto;
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        Event event = findByIdOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("User is not the initiator of the event");
        }

        return requestService.getRequestsByEvent(eventId);
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest request) {

        Event event = findByIdOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("User is not the initiator of the event");
        }

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        long confirmedCount = requestService.getRequestsByEvent(eventId).stream()
                .filter(r -> r.getStatus() == RequestStatus.CONFIRMED)
                .count();

        for (Long requestId : request.getRequestIds()) {
            ParticipationRequest participationRequest = requestService.findByIdOrThrow(requestId);

            if (!participationRequest.getEvent().getId().equals(eventId)) {
                throw new BadRequestException("Request " + requestId + " does not belong to event " + eventId);
            }

            if (participationRequest.getStatus() == RequestStatus.CANCELED
                    || participationRequest.getStatus() == RequestStatus.CONFIRMED
                    || participationRequest.getStatus() == RequestStatus.PENDING) {
                rejected.add(participationRequestMapper.toParticipationRequestDto(participationRequest));
                continue;
            }

            if (request.getStatus() == RequestStatus.CONFIRMED) {
                if (event.getParticipantLimit() != null && event.getParticipantLimit() > 0
                        && confirmedCount >= event.getParticipantLimit()) {
                    rejected.add(participationRequestMapper.toParticipationRequestDto(participationRequest));
                    continue;
                }
                requestService.updateRequestStatus(userId, eventId, requestId, RequestStatus.CONFIRMED);
                confirmedCount++;
                confirmed.add(participationRequestMapper.toParticipationRequestDto(
                        requestService.findByIdOrThrow(requestId)));
            } else if (request.getStatus() == RequestStatus.PENDING) {
                requestService.updateRequestStatus(userId, eventId, requestId, RequestStatus.PENDING);
                rejected.add(participationRequestMapper.toParticipationRequestDto(
                        requestService.findByIdOrThrow(requestId)));
            }
        }

        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }
}

