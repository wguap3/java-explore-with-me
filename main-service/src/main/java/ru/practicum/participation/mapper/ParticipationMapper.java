package ru.practicum.participation.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.participation.dto.ParticipationDtoOut;
import ru.practicum.participation.model.Participation;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ParticipationMapper {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ParticipationDtoOut mapParticipationToParticipationDtoOut(Participation participation) {
        ParticipationDtoOut participationDtoOut = new ParticipationDtoOut();
        participationDtoOut.setCreated(
                participation.getCreated().truncatedTo(ChronoUnit.SECONDS).format(FORMATTER)
        );
        participationDtoOut.setEvent(participation.getEvent());
        participationDtoOut.setId(participation.getId());
        participationDtoOut.setRequester(participation.getRequester());
        participationDtoOut.setStatus(participation.getStatus().toString());
        return participationDtoOut;
    }

    public List<ParticipationDtoOut> mapToDtoList(List<Participation> participations) {
        return participations.stream()
                .map(this::mapParticipationToParticipationDtoOut)
                .toList();
    }
}

