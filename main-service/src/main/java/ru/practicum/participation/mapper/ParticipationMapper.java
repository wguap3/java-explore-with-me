package ru.practicum.participation.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.participation.dto.ParticipationDtoOut;
import ru.practicum.participation.model.Participation;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ParticipationMapper {

    ParticipationMapper INSTANCE = Mappers.getMapper(ParticipationMapper.class);

    @Mapping(target = "created",
            expression = "java(participation.getCreated().truncatedTo(java.time.temporal.ChronoUnit.SECONDS).format(ru.practicum.constants.DateTimeFormatConstants.FORMATTER))")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "status",
            expression = "java(participation.getStatus().toString())")
    ParticipationDtoOut mapParticipationToParticipationDtoOut(Participation participation);

    List<ParticipationDtoOut> mapToDtoList(List<Participation> participations);
}
