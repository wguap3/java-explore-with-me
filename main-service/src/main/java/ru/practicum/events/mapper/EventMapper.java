package ru.practicum.events.mapper;

import org.mapstruct.*;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class})
public interface EventMapper {
    // -------------------- NewEventDto -> Event --------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "lat", source = "location.lat")
    @Mapping(target = "lon", source = "location.lon")
    Event toEvent(NewEventDto dto);

    // -------------------- Event -> NewEventDto --------------------
    @Mapping(target = "category", source = "category.id")
    @Mapping(target = "location.lat", source = "lat")
    @Mapping(target = "location.lon", source = "lon")
    NewEventDto toNewEventDto(Event event);

    // -------------------- Event -> EventShortDto --------------------
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    EventShortDto toEventShortDto(Event event);

    // -------------------- Event -> EventFullDto --------------------
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "location.lat", source = "lat")
    @Mapping(target = "location.lon", source = "lon")
    EventFullDto toEventFullDto(Event event);

    // -------------------- UpdateEventUserRequest -> Event --------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true) // категорию отдельно нужно ставить через сервис
    @Mapping(target = "lat", source = "location.lat")
    @Mapping(target = "lon", source = "location.lon")
    void updateEventFromUser(UpdateEventUserRequest dto, @MappingTarget Event event);

    // -------------------- UpdateEventAdminRequest -> Event --------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true) // категорию отдельно нужно ставить через сервис
    @Mapping(target = "lat", source = "location.lat")
    @Mapping(target = "lon", source = "location.lon")
    void updateEventFromAdmin(UpdateEventAdminRequest dto, @MappingTarget Event event);

    // -------------------- Event -> LocationDto --------------------
    @Mapping(target = "lat", source = "lat")
    @Mapping(target = "lon", source = "lon")
    LocationDto toLocationDto(Event event);

    // -------------------- LocationDto -> Event coordinates --------------------
    @Mapping(target = "lat", source = "lat")
    @Mapping(target = "lon", source = "lon")
    void updateEventLocationFromDto(LocationDto locationDto, @MappingTarget Event event);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(target = "location", expression = "java(new LocationDto(event.getLat(), event.getLon()))")
    EventResponseDto toResponseDto(Event event);

    ;

}
