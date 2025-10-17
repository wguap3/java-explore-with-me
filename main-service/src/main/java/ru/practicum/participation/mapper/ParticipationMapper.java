package ru.practicum.participation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.participation.dto.ParticipationDtoOut;
import ru.practicum.participation.model.Participation;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring")
public interface ParticipationMapper {

    ParticipationMapper INSTANCE = Mappers.getMapper(ParticipationMapper.class);

    @Mapping(target = "created", expression = "java(formatCreated(participation.getCreated()))")
    @Mapping(target = "status", source = "status")
    ParticipationDtoOut mapParticipationToParticipationDtoOut(Participation participation);

    default String formatCreated(java.time.LocalDateTime created) {
        return created.truncatedTo(ChronoUnit.MILLIS)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
    }
}