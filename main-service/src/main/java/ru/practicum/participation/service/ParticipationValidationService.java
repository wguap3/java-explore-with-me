package ru.practicum.participation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.enums.EveState;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ConflictException;
import ru.practicum.participation.dto.ParticipationUpdateDtoIn;
import ru.practicum.participation.model.Participation;
import ru.practicum.participation.repository.ParticipationRepository;
import ru.practicum.user.model.User;

@Service
@RequiredArgsConstructor
public class ParticipationValidationService {

    private final ParticipationRepository participationRepository;

    public void validateParticipationUpdate(Event event, ParticipationUpdateDtoIn dto) {
        if (dto == null) {
            throw new ConflictException("Тело запроса пустое! Нарушение спецификации!");
        }

        Long number = participationRepository.countByEvent(event.getId());
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= number) {
            throw new ConflictException("Достигнут лимит по заявкам на данное событие!");
        }

        if (participationRepository.countBadReq(dto.getRequestIds()) > 0) {
            throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания!");
        }
    }

    public void validateAddParticipation(User user, Event event, Long currentCount) {
        if (event.getInitiator().equals(user.getId())) {
            throw new ConflictException("Инициатор события не может добавить запрос на своё событие!");
        }

        if (!event.getState().equals(EveState.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии!");
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= currentCount) {
            throw new ConflictException("Достигнут лимит по заявкам на данное событие!");
        }
    }

    public void validateCancelParticipation(Participation participation, Long userId) {
        if (!participation.getRequester().equals(userId)) {
            throw new ConflictException("Нельзя отменить чужой запрос на участие в событии!");
        }
    }
}
