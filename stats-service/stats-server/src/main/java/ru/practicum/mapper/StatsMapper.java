package ru.practicum.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHitEntity;


@Mapper(componentModel = "spring")
public interface StatsMapper {
    EndpointHitDto toEndpointHitDto(EndpointHitEntity endpointHitEntity);

    EndpointHitEntity toEndpointHitEntity(EndpointHitDto endopointHitDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(EndpointHitDto dto, @MappingTarget EndpointHitEntity entity);
}
