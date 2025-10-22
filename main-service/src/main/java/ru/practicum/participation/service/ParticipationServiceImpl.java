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
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {
    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final ParticipationValidationService participationValidationService;
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
    public ParticipationUpdateDtoOut updateEventRequests(Long userId, Long eventId, ParticipationUpdateDtoIn dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        participationValidationService.validateParticipationUpdate(event, dto);

        ParticipationUpdateDtoOut out = new ParticipationUpdateDtoOut();

        Long freePlaces = event.getParticipantLimit() - participationRepository.countByEvent(eventId);

        if (dto.getStatus() == UpdateState.REJECTED) {
            updateStatus(dto.getRequestIds(), PartState.REJECTED, out);
        } else if (dto.getStatus() == UpdateState.CONFIRMED) {
            int size = dto.getRequestIds().size();
            if (freePlaces >= size) {
                updateStatus(dto.getRequestIds(), PartState.CONFIRMED, out);
            } else {
                updateStatus(dto.getRequestIds().subList(0, freePlaces.intValue()), PartState.CONFIRMED, out);
                updateStatus(dto.getRequestIds().subList(freePlaces.intValue(), size), PartState.REJECTED, out);
            }
        }

        return out;
    }

    private void updateStatus(List<Long> requestIds, PartState status, ParticipationUpdateDtoOut out) {
        List<Participation> participations = requestIds.stream()
                .map(id -> participationRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Participation with id=" + id + " was not found")))
                .toList();

        participations.forEach(p -> p.setStatus(status));
        participationRepository.saveAll(participations);

        if (status == PartState.CONFIRMED) {
            out.getConfirmedRequests().addAll(participationMapper.mapToDtoList(participations));
        } else if (status == PartState.REJECTED) {
            out.getRejectedRequests().addAll(participationMapper.mapToDtoList(participations));
        }
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

    private void savePart(Long eventId, Integer start, Integer size, List<Participation> list,
                          PartState status, ParticipationUpdateDtoIn participationUpdateDtoIn,
                          ParticipationUpdateDtoOut participationUpdateDtoOut) {

        List<Participation> toUpdate = participationUpdateDtoIn.getRequestIds().subList(start, size)
                .stream()
                .map(id -> participationRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Participation with id=" + id + " was not found")))
                .collect(Collectors.toList());
        toUpdate.forEach(part -> part.setStatus(status));

        participationRepository.saveAll(toUpdate);


        if (status == PartState.CONFIRMED) {
            List<ParticipationDtoOut> confirmed = toUpdate.stream()
                    .map(participationMapper::mapParticipationToParticipationDtoOut)
                    .toList();
            if (participationUpdateDtoOut.getConfirmedRequests() == null) {
                participationUpdateDtoOut.setConfirmedRequests(new ArrayList<>());
            }
            participationUpdateDtoOut.getConfirmedRequests().addAll(confirmed);

        } else if (status == PartState.REJECTED) {
            List<ParticipationDtoOut> rejected = toUpdate.stream()
                    .map(participationMapper::mapParticipationToParticipationDtoOut)
                    .toList();
            if (participationUpdateDtoOut.getRejectedRequests() == null) {
                participationUpdateDtoOut.setRejectedRequests(new ArrayList<>());
            }
            participationUpdateDtoOut.getRejectedRequests().addAll(rejected);
        }
    }

}
