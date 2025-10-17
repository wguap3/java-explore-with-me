package ru.practicum.participation.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.participation.dto.ParticipationDtoOut;
import ru.practicum.participation.model.Participation;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring")
public interface ParticipationMapper {

    @Mapping(
            target = "created",
            expression = "java(formatCreated(participation.getCreated()))"
    )
    @Mapping(target = "event", source = "event")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "status", expression = "java(participation.getStatus().toString())")
    ParticipationDtoOut mapParticipationToParticipationDtoOut(Participation participation);

    default String formatCreated(java.time.LocalDateTime created) {
        if (created == null) return null;
        return created
                .truncatedTo(ChronoUnit.MILLIS)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
    }
}
