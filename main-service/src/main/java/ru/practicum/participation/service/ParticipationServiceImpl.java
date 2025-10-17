package ru.practicum.participation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.enums.EveState;
import ru.practicum.enums.PartState;
import ru.practicum.enums.UpdateState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.participation.dto.ParticipationDtoOut;
import ru.practicum.participation.dto.ParticipationUpdateDtoIn;
import ru.practicum.participation.dto.ParticipationUpdateDtoOut;
import ru.practicum.participation.mapper.ParticipationMapper;
import ru.practicum.participation.model.Participation;
import ru.practicum.participation.repository.ParticipationRepository;
import ru.practicum.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {
    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final ParticipationMapper participationMapper;

    @Override
    public List<ParticipationDtoOut> getUserParticipation(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getInitiator().equals(userId)) {
            throw new BadRequestException("Пользователь не является инициатором события");
        }
        return participationRepository.findAllByEvent(eventId).stream().map(participationMapper::mapParticipationToParticipationDtoOut).toList();
    }

    @Transactional
    @Override
    public ParticipationUpdateDtoOut updateEventRequests(Long userId, Long eventId, ParticipationUpdateDtoIn participationUpdateDtoIn) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        Long number = participationRepository.countByEvent(eventId);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= number) {
            throw new ConflictException("Достигнут лимит по заявкам на данное событие! - updateEventRequests");
        }
        if (participationUpdateDtoIn == null) {
            throw new ConflictException("Тело пустое!Нарушение спецификации!"); //ошибка тестов
        }
        if (participationRepository.countBadReq(participationUpdateDtoIn.getRequestIds()) > 0) {
            throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания!");
        }
        ParticipationUpdateDtoOut participationUpdateDtoOut = new ParticipationUpdateDtoOut();
        List<Participation> list = new ArrayList<>();
        if (participationUpdateDtoIn.getStatus().equals(UpdateState.REJECTED)) { //если отмена
            list = participationRepository.participationReq(participationUpdateDtoIn.getRequestIds());
            List<Participation> rejectList = list;
            List<Participation> statusList = list;
            statusList.forEach(part -> part.setStatus(PartState.REJECTED));
            statusList.forEach(participationRepository::save);
            participationUpdateDtoOut.setConfirmedRequests(new ArrayList<>());
            participationUpdateDtoOut.setRejectedRequests(rejectList.stream().map(participationMapper::mapParticipationToParticipationDtoOut).toList());
        } else if (participationUpdateDtoIn.getStatus().equals(UpdateState.CONFIRMED)) {
            Long freePlaces = event.getParticipantLimit() - number; //количесво свободных мест
            if (freePlaces >= participationUpdateDtoIn.getRequestIds().size()) { //свободных мест достаточно
                savePart(eventId, 0, participationUpdateDtoIn.getRequestIds().size(), list, PartState.CONFIRMED, participationUpdateDtoIn, participationUpdateDtoOut);
            } else {
                int numberToApprove = (int) (participationUpdateDtoIn.getRequestIds().size() - freePlaces);
                savePart(eventId, 0, numberToApprove, list, PartState.CONFIRMED, participationUpdateDtoIn, participationUpdateDtoOut);
                List<Participation> rejectList = new ArrayList<>(List.of());
                savePart(eventId, numberToApprove, participationUpdateDtoIn.getRequestIds().size(), rejectList, PartState.REJECTED, participationUpdateDtoIn, participationUpdateDtoOut);
            }
        }
        return participationUpdateDtoOut;
    }

    @Override
    public List<ParticipationDtoOut> getParticipationForAnotherUser(Long userId) {
        userService.getUser(userId);
        return participationRepository.findAllByRequester(userId).stream().map(participationMapper::mapParticipationToParticipationDtoOut).toList();
    }

    @Transactional
    @Override
    public ParticipationDtoOut addParticipation(Long userId, Long eventId) {
        if (!participationRepository.findAllByRequesterAndEvent(userId, eventId).isEmpty()) {
            throw new ConflictException("Нельзя добавить повторный запрос!");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getInitiator().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии!");
        }
        if (!event.getState().equals(EveState.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии!");
        }
        Long number = participationRepository.countByEvent(eventId);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= number) {
            throw new ConflictException("Достигнут лимит по заявкам на данное событие! - addParticipation");
        }
        Participation participation = new Participation();
        participation.setEvent(eventId);
        participation.setRequester(userId);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            participation.setStatus(PartState.CONFIRMED);
        } else {
            participation.setStatus(PartState.PENDING);
        }
        return participationMapper.mapParticipationToParticipationDtoOut(participationRepository.save(participation));
    }

    @Transactional
    @Override
    public ParticipationDtoOut cancelParticipation(Long userId, Long requestId) {
        Participation participation = participationRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        if (!participation.getRequester().equals(userId)) {
            throw new ConflictException("Нельзя отменить чужой запрос на участие в событии!");
        }
        participation.setStatus(PartState.CANCELED);
        return participationMapper.mapParticipationToParticipationDtoOut(participationRepository.save(participation));
    }

    public void savePart(Long eventId, Integer start, Integer size, List<Participation> list, PartState status, ParticipationUpdateDtoIn participationUpdateDtoIn, ParticipationUpdateDtoOut participationUpdateDtoOut) {
        for (int i = start; i < size; i++) {
            Participation participation = participationRepository.findById(participationUpdateDtoIn.getRequestIds().get(i))
                    .orElseThrow(() -> new NotFoundException("Participation with id=" + eventId + " was not found"));
            list.add(participation);
        }
        list.forEach(participation -> participation.setStatus(status));
        list.forEach(participationRepository::save);
        participationUpdateDtoOut.setConfirmedRequests(list.stream().map(participationMapper::mapParticipationToParticipationDtoOut).toList());
        participationUpdateDtoOut.setRejectedRequests(new ArrayList<>());
    }

}
