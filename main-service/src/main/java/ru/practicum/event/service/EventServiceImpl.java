package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.enums.EveState;
import ru.practicum.enums.SortMode;
import ru.practicum.enums.StateAction;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventDtoOut;
import ru.practicum.event.dto.EventShortDtoOut;
import ru.practicum.event.dto.EventUpdateDtoIn;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.participation.repository.ParticipationRepository;
import ru.practicum.stats.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final StatsService statsService;
    private final ParticipationRepository participationRepository;

    @Transactional
    @Override
    public EventDtoOut addEvent(Long userId, EventDtoIn eventDtoIn, HttpServletRequest request) {
        Event event = eventMapper.mapEventDtoInToEvent(eventDtoIn);
        checkValidTime(event.getEventDate(), 2, "Дата и время на которые намечено событие не может быть раньше, чем 2 часа от текущего момента");
        event.setInitiator(userId);
        return eventMapper.mapEventToEventDtoOut(eventRepository.save(event));
    }

    @Override
    public List<EventShortDtoOut> getEvents(Long userId, Integer from, Integer size) {
        List<Event> events = eventRepository.getEvents(userId, from, size);

        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .toList();

        final Map<String, Long> viewsMap = new HashMap<>();
        try {
            List<ViewStatsDto> stats = statsService.getStats(
                    LocalDateTime.now().minusYears(10),
                    LocalDateTime.now(),
                    uris,
                    true
            );
            if (stats != null) {
                stats.forEach(s -> viewsMap.put(s.getUri(), s.getHits()));
            }
        } catch (Exception ex) {
            log.error("Ошибка получения просмотров для событий: {}", uris, ex);
        }

        return events.stream()
                .map(event -> {
                    Long views = viewsMap.getOrDefault("/events/" + event.getId(), 0L);
                    return eventMapper.mapEventToEventShortDtoOut(event, views);
                })
                .toList();
    }


    @Override
    public EventDtoOut getFullEvent(Long userId, Long eventId) {
        checkEvent(eventId);
        return eventMapper.mapEventToEventDtoOut(eventRepository.findByIdAndInitiator(eventId, userId).orElseThrow(() -> new BadRequestException("Это событие не добавлено выбранным пользователем!")));
    }

    @Transactional
    @Override
    public EventDtoOut updateEvent(Long userId, Long eventId, EventUpdateDtoIn eventDtoIn) {
        checkEvent(eventId);
        Event event = eventRepository.findByIdAndInitiator(eventId, userId).orElseThrow(() -> new BadRequestException("Это событие не добавлено выбранным пользователем!"));
        if (event.getState().equals(EveState.PUBLISHED)) {
            throw new ConflictException("Event must not be published");
        } else if (!event.getState().equals(EveState.PENDING) && !event.getState().equals(EveState.CANCELED)) {
            throw new ForbiddenException("Only pending or canceled events can be changed");
        }
        eventMapper.updateEventFromDto(event, eventDtoIn);
        if (eventDtoIn.getStateAction() != null && eventDtoIn.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            event.setState(EveState.CANCELED);
        } else if (eventDtoIn.getStateAction() != null && eventDtoIn.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            event.setState(EveState.PENDING);
        }
        return eventMapper.mapEventToEventDtoOut(eventRepository.save(event));
    }

    @Override
    public List<EventShortDtoOut> getPublicEvent(String text, Long[] categories, Boolean paid,
                                                 String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                 String sort, Integer from, Integer size, HttpServletRequest request) {

        log.info("getPublicEvent called with parameters: text='{}', categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        statsService.sendHit("main-service", "/events", request.getRemoteAddr());

        String lowText = text != null ? text.toLowerCase().replace("\"", "") : "";

        List<Event> events;
        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime start = parseDate(rangeStart);
            LocalDateTime end = parseDate(rangeEnd);
            if (start.isAfter(end)) {
                throw new BadRequestException("Дата начала события позже даты конца события!");
            }
            events = eventRepository.getPublicEventByTextAndStartAndEnd(lowText, start, end);
        } else if (rangeStart != null) {
            events = eventRepository.getPublicEventByTextAndStart(lowText, parseDate(rangeStart));
        } else if (rangeEnd != null) {
            events = eventRepository.getPublicEventByTextAndEnd(lowText, parseDate(rangeEnd));
        } else {
            events = eventRepository.getPublicEventByText(lowText);
        }

        if (paid != null) {
            events = events.stream()
                    .filter(e -> e.getPaid() != null && e.getPaid().equals(paid))
                    .toList();
        }

        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = events.stream()
                    .filter(e -> e.getParticipantLimit() == 0 || participationRepository.countByEventIdAndConfirmed(e.getId()) < e.getParticipantLimit())
                    .toList();
        }

        if (categories != null && categories.length > 0) {
            List<Long> categoryList = Arrays.asList(categories);
            events = events.stream()
                    .filter(e -> categoryList.contains(e.getCategory()))
                    .toList();
        }

        List<String> uris = events.stream().map(e -> "/events/" + e.getId()).toList();
        final Map<String, Long> finalViewsMap = new HashMap<>();
        try {
            List<ViewStatsDto> stats = statsService.getStats(
                    LocalDateTime.now().minusYears(10), LocalDateTime.now(), uris, true);
            if (stats != null) {
                finalViewsMap.putAll(stats.stream()
                        .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits)));
            }
        } catch (Exception ex) {
            log.error("Ошибка получения просмотров для событий: {}", uris, ex);
        }

        List<EventShortDtoOut> result = events.stream()
                .map(event -> eventMapper.mapEventToEventShortDtoOut(
                        event,
                        finalViewsMap.getOrDefault("/events/" + event.getId(), 0L)
                ))
                .toList();

        SortMode sortMode = SortMode.fromString(sort);
        if (sortMode == SortMode.VIEWS) {
            result = result.stream()
                    .sorted(Comparator.comparing(EventShortDtoOut::getViews, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                    .toList();
        } else { // EVENT_DATE
            result = result.stream()
                    .sorted(Comparator.comparing(EventShortDtoOut::getEventDate))
                    .toList();
        }

        // 9️⃣ Пагинация
        int startIndex = Math.min(from != null ? from : 0, result.size());
        int endIndex = Math.min(startIndex + (size != null ? size : result.size()), result.size());

        return result.subList(startIndex, endIndex);
    }


    @Override
    public EventDtoOut getPublicEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.getPublicEventById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        EventDtoOut eventDtoOut = eventMapper.mapEventToEventDtoOut(event);
        try {
            statsService.sendHitId(
                    eventId,
                    "main-service",
                    "/events/" + eventId,
                    request.getRemoteAddr()
            );
        } catch (Exception ex) {
            log.error("Ошибка отправки статистики для события id={}", eventId, ex);
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(eventDtoOut.getCreatedOn(), formatter);
            LocalDateTime endTime = LocalDateTime.now();
            List<String> uris = Collections.singletonList("/events/" + eventId);

            List<ViewStatsDto> stats = statsService.getStats(startTime, endTime, uris, true);

            eventDtoOut.setViews(stats.isEmpty() ? 0 : stats.get(0).getHits());
        } catch (Exception ex) {
            log.error("Ошибка получения просмотров для события id={}", eventId, ex);
            eventDtoOut.setViews(0L);
        }
        return eventDtoOut;
    }

    @Override
    public List<EventDtoOut> getAdminEvent(Long[] users, String[] states, Long[] categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        List<Long> ids = List.of();
        if (users != null) {
            ids = List.of(users);
        }

        List<Long> nextIds = List.of();
        if (categories != null && !ids.isEmpty()) {
            nextIds = eventRepository.getAdminEventsByIdsAndCategory(ids, categories).stream().map(Event::getId).toList();
        } else if (categories != null && ids.isEmpty()) {
            nextIds = eventRepository.getAdminEventsByCategory(categories).stream().map(Event::getId).toList();
        } else if (categories == null && !ids.isEmpty()) {
            nextIds = eventRepository.getAdminEventsByIds(ids).stream().map(Event::getId).toList();
        }

        if ((categories != null || !ids.isEmpty()) && nextIds.isEmpty()) { //ничего не нашли
            return new ArrayList<>();
        }

        //фильтруем по состояниям
        List<Long> stepThreeIds = List.of();
        if (states != null) {
            List<String> goodStates = List.of(states);
            if (nextIds.isEmpty()) {
                stepThreeIds = eventRepository.getAdminEventsInStates(goodStates).stream().map(Event::getId).toList();
            } else {
                stepThreeIds = eventRepository.getAdminEventsInIdsAndStates(nextIds, goodStates).stream().map(Event::getId).toList();
            }
        } else {
            if (!nextIds.isEmpty()) {
                stepThreeIds = nextIds;
            }
        }

        if (states != null && stepThreeIds.isEmpty()) {
            return new ArrayList<>();
        }

        //делаем выборку по датам
        List<Event> events;
        if (stepThreeIds.isEmpty()) {
            if (rangeStart != null && rangeEnd != null) {
                events = eventRepository.getAdminEventByStartAndEnd(parseDate(rangeStart), parseDate(rangeEnd), from, size);
            } else if (rangeStart != null && rangeEnd == null) {
                events = eventRepository.getAdminEventByStart(parseDate(rangeStart), from, size);
            } else if (rangeStart == null && rangeEnd != null) {
                events = eventRepository.getAdminEventByEnd(parseDate(rangeEnd), from, size);
            } else {
                events = eventRepository.getAdminEvent(from, size);
            }
        } else {
            if (rangeStart != null && rangeEnd != null) {
                events = eventRepository.getAdminEventInIdsByStartAndEnd(stepThreeIds, parseDate(rangeStart), parseDate(rangeEnd), from, size);
            } else if (rangeStart != null && rangeEnd == null) {
                events = eventRepository.getAdminEventInIdsByStart(stepThreeIds, parseDate(rangeStart), from, size);
            } else if (rangeStart == null && rangeEnd != null) {
                events = eventRepository.getAdminEventInIdsByEnd(stepThreeIds, parseDate(rangeEnd), from, size);
            } else {
                events = eventRepository.getAdminEventInIds(stepThreeIds, from, size);
            }
        }
        return events.stream().map(eventMapper::mapEventToEventDtoOut).toList();
    }


    @Transactional
    @Override
    public EventDtoOut updateAdminEvent(Long eventId, EventUpdateDtoIn eventDtoIn) {
        Event event = getEvent(eventId);
        if (event.getState().equals(EveState.PUBLISHED) || event.getState().equals(EveState.CANCELED)) {
            throw new ConflictException("Event must not be published or canceled");
        }
        LocalDateTime date = event.getEventDate();
        checkValidTime(date, 1, "Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        eventMapper.updateEventFromDto(event, eventDtoIn);
        if (eventDtoIn.getStateAction() != null && eventDtoIn.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            event.setState(EveState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else if (eventDtoIn.getStateAction() != null && eventDtoIn.getStateAction().equals(StateAction.REJECT_EVENT)) {
            event.setState(EveState.CANCELED);
        }
        return eventMapper.mapEventToEventDtoOut(eventRepository.save(event));
    }

    @Override
    public List<EventShortDtoOut> getCompilationsEvents(List<Long> eventIds) {
        List<Event> events = eventRepository.getCompilationsEvents(eventIds);

        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .toList();

        final Map<String, Long> viewsMap = new HashMap<>();
        try {
            List<ViewStatsDto> stats = statsService.getStats(
                    LocalDateTime.now().minusYears(10), LocalDateTime.now(), uris, true
            );
            if (stats != null) {
                stats.forEach(s -> viewsMap.put(s.getUri(), s.getHits()));
            }
        } catch (Exception ex) {
            log.error("Ошибка получения просмотров для событий: {}", uris, ex);
        }

        return events.stream()
                .map(event -> {
                    Long views = viewsMap.getOrDefault("/events/" + event.getId(), 0L);
                    return eventMapper.mapEventToEventShortDtoOut(event, views);
                })
                .sorted(Comparator.comparing(EventShortDtoOut::getViews, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
    }


    public void checkValidTime(LocalDateTime time, Integer hours, String string) {
        if (LocalDateTime.now().plusHours(hours).isAfter(time)) {
            throw new BadRequestException(string);
        }
    }

    public void checkEvent(Long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    private LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date.replace("\"", ""), FORMATTER);
    }

}
