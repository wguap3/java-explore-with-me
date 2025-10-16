package ru.practicum.events.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.events.dto.*;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventStatus;
import ru.practicum.events.model.SortType;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapper.ParticipationRequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.ParticipationRequestRepository;
import ru.practicum.requests.service.ParticipationRequestService;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ParticipationRequestService requestService;
    private final ParticipationRequestMapper participationRequestMapper;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsClient statsClient;

    @Override
    public EventResponseDto addEvent(Long userId, NewEventDto newEventDto) {
        User initiator = userService.findByIdOrThrow(userId);
        Category category = categoryService.findByIdOrThrow(newEventDto.getCategory());

        Event event = eventMapper.toEvent(newEventDto);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setState(EventStatus.PENDING);
        event.setCreatedOn(LocalDateTime.now());

        return eventMapper.toResponseDto(eventRepository.save(event));
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
                case SEND_TO_REVIEW -> {
                    if (event.getState() == EventStatus.PUBLISHED) {
                        throw new BadRequestException("Cannot send a published event to review");
                    }
                    event.setState(EventStatus.PENDING);
                }
                case CANCEL_REVIEW -> {
                    if (event.getState() != EventStatus.PENDING) {
                        throw new BadRequestException("Only events in PENDING state can be canceled");
                    }
                    event.setState(EventStatus.CANCELED);
                }
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

        LocalDateTime start = parseDate(rangeStart);
        LocalDateTime end = parseDate(rangeEnd);

        PageRequest page = PageRequest.of(from / size, size);

        return eventRepository.findAdminEvents(userIds, statusList, categories, start, end, page).stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            return LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        }
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
                    if (event.getState() != EventStatus.PENDING) {
                        throw new BadRequestException("Событие должно быть в ожидании публикации");
                    }
                    if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                        throw new BadRequestException("Дата начала события должна быть не ранее чем через час от публикации");
                    }
                    event.setState(EventStatus.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> {
                    if (event.getState() != EventStatus.PENDING) {
                        throw new BadRequestException("Можно отклонить только события в ожидании публикации");
                    }
                    event.setState(EventStatus.CANCELED);
                }
            }
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public Event findByIdOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));
    }

    public List<EventShortDto> findEvents(String text,
                                          List<Integer> categories,
                                          Boolean paid,
                                          String rangeStart,
                                          String rangeEnd,
                                          Boolean onlyAvailable,
                                          SortType sort,
                                          Integer from,
                                          Integer size,
                                          HttpServletRequest request) {

        LocalDateTime start = parseDate(rangeStart);
        LocalDateTime end = parseDate(rangeEnd);
        text = text != null ? "%" + text + "%" : "%";

        String sortField = sort == SortType.VIEWS ? "views" : "eventDate";
        Sort sortBy = Sort.by(sortField);

        Pageable pageable = PageRequest.of(from / size, size, sortBy);

        Page<Event> events;
        if (start == null || end == null) {
            events = eventRepository.findEventsWithFiltersWithoutDate(text, categories, paid, onlyAvailable, LocalDateTime.now(), pageable);
        } else {
            events = eventRepository.findEventsWithFilters(text, categories, paid, start, end, onlyAvailable, pageable);
        }

        statsClient.saveHit(new EndpointHitDto(
                null,
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));

        List<EventShortDto> list = events.stream()
                .map(eventMapper::toEventShortDto)
                .toList();

        return list;
    }


    @Override
    @Transactional
    public EventFullDto findEventById(long id, HttpServletRequest request) {

        Event event = eventRepository.findById(id).orElse(null);

        if (event == null || event.getState() != EventStatus.PUBLISHED) {
            throw new NotFoundException("Event with id=" + id + " not found");
        }

        if (!statsClient.existsByIpAndUri(request.getRemoteAddr(), request.getRequestURI())) {
            event.setViews(event.getViews() + 1);
            event = eventRepository.save(event);
        }

        statsClient.saveHit(new EndpointHitDto(null,
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));

        return eventMapper.toEventFullDto(event);
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

        long confirmedCount = participationRequestRepository.findAllByEventId(eventId).stream()
                .filter(r -> r.getStatus() == RequestStatus.CONFIRMED)
                .count();

        for (Long requestId : request.getRequestIds()) {
            ParticipationRequest participationRequest = requestService.findByIdOrThrow(requestId);

            if (!participationRequest.getEvent().getId().equals(eventId)) {
                throw new BadRequestException("Request " + requestId + " does not belong to event " + eventId);
            }

            if (participationRequest.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Only pending requests can be changed");
            }

            if (request.getStatus() == RequestStatus.REJECTED) {
                participationRequest.setStatus(RequestStatus.REJECTED);
                rejected.add(participationRequestMapper.toParticipationRequestDto(participationRequest));
                continue;
            }

            if (request.getStatus() == RequestStatus.CONFIRMED) {
                if (event.getParticipantLimit() != null && event.getParticipantLimit() > 0
                        && confirmedCount >= event.getParticipantLimit()) {
                    participationRequest.setStatus(RequestStatus.REJECTED);
                    rejected.add(participationRequestMapper.toParticipationRequestDto(participationRequest));
                    continue;
                }

                participationRequest.setStatus(RequestStatus.CONFIRMED);
                confirmedCount++;
                confirmed.add(participationRequestMapper.toParticipationRequestDto(participationRequest));
            }

            participationRequestRepository.save(participationRequest);
        }

        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

}

