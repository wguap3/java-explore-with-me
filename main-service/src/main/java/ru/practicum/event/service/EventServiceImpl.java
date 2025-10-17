package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.enums.EveState;
import ru.practicum.enums.StateAction;
import ru.practicum.event.controller.StatClient;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final StatClient statsClient;

    @Transactional
    @Override
    public EventDtoOut addEvent(Long userId, EventDtoIn eventDtoIn) {
        Event event = eventMapper.mapEventDtoInToEvent(eventDtoIn);
        checkValidTime(event.getEventDate(), 2, "Дата и время на которые намечено событие не может быть раньше, чем 2 часа от текущего момента");
        event.setInitiator(userId);
        return eventMapper.mapEventToEventDtoOut(eventRepository.save(event));
    }

    @Override
    public List<EventShortDtoOut> getEvents(Long userId, Integer from, Integer size) {
        return eventRepository.getEvents(userId, from, size).stream().map(eventMapper::mapEventToEventShortDtoOut).toList();
        // .sorted(Comparator.comparing(EventShortDtoOut::getViews, Comparator.nullsLast(Comparator.naturalOrder())).reversed()).toList();
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
        changeEventFields(event, eventDtoIn);
        if (eventDtoIn.getStateAction() != null && eventDtoIn.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            event.setState(EveState.CANCELED);
        } else if (eventDtoIn.getStateAction() != null && eventDtoIn.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            event.setState(EveState.PENDING);
        }
        return eventMapper.mapEventToEventDtoOut(eventRepository.save(event));
    }

    @Override
    public List<EventShortDtoOut> getPublicEvent(String text, Long[] categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size) {
        log.info("getPublicEvent called with parameters: text='{}', categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        statsClient.sendHit();

        String lowText = text.toLowerCase().replace("\"", "");

        List<Event> events;
        if (rangeStart != null && rangeEnd != null) {
            if (parseDate(rangeStart).isAfter(parseDate(rangeEnd))) {
                throw new BadRequestException("Дата начала события позже даты конца события!");
            }
            events = eventRepository.getPublicEventByTextAndStartAndEnd(lowText, parseDate(rangeStart), parseDate(rangeEnd));
        } else if (rangeStart != null) {
            events = eventRepository.getPublicEventByTextAndStart(lowText, parseDate(rangeStart));
        } else if (rangeEnd != null) {
            events = eventRepository.getPublicEventByTextAndEnd(lowText, parseDate(rangeEnd));
        } else {
            events = eventRepository.getPublicEventByText(lowText);
        }

        // Фильтр по оплате
        List<Event> firstEvents;
        if (paid != null) {
            firstEvents = events.stream().filter(event -> event.getPaid() != null && event.getPaid().equals(paid)).toList();

        } else {
            firstEvents = events;
        }

        // Фильтр по доступности
        List<Long> ids;
        if (onlyAvailable != null && onlyAvailable) {
            List<EventDtoOut> nextEvents = firstEvents.stream()
                    .map(eventMapper::mapEventToEventDtoOut)
                    .toList();
            ids = nextEvents.stream()
                    .filter(eventDtoOut -> eventDtoOut.getConfirmedRequests() != null
                            && eventDtoOut.getParticipantLimit() != null
                            && eventDtoOut.getConfirmedRequests() < eventDtoOut.getParticipantLimit())
                    .map(EventDtoOut::getId)
                    .toList();
        } else {
            ids = firstEvents.stream().map(Event::getId).toList();
        }

        // Фильтр по категориям и сортировка
        List<EventShortDtoOut> result;
        if (categories != null && categories.length > 0) {
            if ("EVENT_DATE".equals(sort)) {
                result = eventRepository.getEventsSortDateAndCategory(ids, categories, from, size).stream()
                        .map(eventMapper::mapEventToEventShortDtoOut)
                        .toList();
            } else {
                result = eventRepository.getEventsSortViewsAndCategory(ids, categories, from, size).stream()
                        .map(eventMapper::mapEventToEventShortDtoOut)
                        .sorted(Comparator.comparing(EventShortDtoOut::getViews, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                        .toList();
            }
        } else {
            if ("EVENT_DATE".equals(sort)) {
                result = eventRepository.getEventsSortDate(ids, from, size).stream()
                        .map(eventMapper::mapEventToEventShortDtoOut)
                        .toList();
            } else {
                result = eventRepository.getEventsSortViews(ids, from, size).stream()
                        .map(eventMapper::mapEventToEventShortDtoOut)
                        .sorted(Comparator.comparing(EventShortDtoOut::getViews, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                        .toList();
            }
        }
        return result;
    }


    @Override
    public EventDtoOut getPublicEventById(Long eventId) {
        Event event = eventRepository.getPublicEventById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState().equals(EveState.PUBLISHED)) {
            statsClient.sendHitId(eventId);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Ошибка задержки: " + e.getMessage(), e);
            }
        }
        EventDtoOut eventDtoOut = eventMapper.mapEventToEventDtoOut(event);
        String start = eventDtoOut.getCreatedOn();
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String[] uris = {"/events/" + event.getId()};
        LocalDateTime startTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<String> uriList = Arrays.asList(uris);

        List<ViewStatsDto> stats = statsClient.getStats(startTime, endTime, uriList, true);

        if (stats != null && !stats.isEmpty()) {
            eventDtoOut.setViews(stats.get(0).getHits().intValue());
        } else {
            eventDtoOut.setViews(0);
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

        if (states != null && stepThreeIds.isEmpty()) { //ничего не нашли
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
        changeEventFields(event, eventDtoIn);
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
        return eventRepository.getCompilationsEvents(eventIds).stream().map(eventMapper::mapEventToEventShortDtoOut)
                .sorted(Comparator.comparing(EventShortDtoOut::getViews, Comparator.nullsLast(Comparator.naturalOrder())).reversed()).toList();
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

    public LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date.replace("\"", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void changeEventFields(Event event, EventUpdateDtoIn eventDtoIn) {
        if (eventDtoIn.getAnnotation() != null) {
            event.setAnnotation(eventDtoIn.getAnnotation());
        }
        if (eventDtoIn.getCategory() != null) {
            event.setCategory(eventDtoIn.getCategory());
        }
        if (eventDtoIn.getDescription() != null) {
            event.setDescription(eventDtoIn.getDescription());
        }
        if (eventDtoIn.getEventDate() != null) {
            checkValidTime(event.getEventDate(), 2, "Дата и время на которые намечено событие не может быть раньше, чем 2 часа от текущего момента");
            checkValidTime(LocalDateTime.parse(eventDtoIn.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 0, "Дата и время события не может быть в прошлом");
            event.setEventDate(parseDate(eventDtoIn.getEventDate()));
        }
        if (eventDtoIn.getLocation() != null) {
            event.setLocationLat(eventDtoIn.getLocation().getLat());
            event.setLocationLon(eventDtoIn.getLocation().getLon());
        }
        if (eventDtoIn.getPaid() != null) {
            event.setPaid(eventDtoIn.getPaid());
        }
        if (eventDtoIn.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDtoIn.getParticipantLimit());
        }
        if (eventDtoIn.getRequestModeration() != null) {
            event.setRequestModeration(eventDtoIn.getRequestModeration());
        }
        if (eventDtoIn.getTitle() != null) {
            event.setTitle(eventDtoIn.getTitle());
        }
    }
}
