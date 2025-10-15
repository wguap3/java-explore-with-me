package ru.practicum.requests.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.events.model.Event;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {

    // -------------------- Entity -> DTO --------------------
    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "created", source = "created")
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);

    // -------------------- DTO -> Entity --------------------
    @Mapping(target = "requester", source = "requester", qualifiedByName = "mapUser")
    @Mapping(target = "event", source = "event", qualifiedByName = "mapEvent")
    @Mapping(target = "created", ignore = true)
    // создаётся через @PrePersist
    ParticipationRequest toParticipationRequest(ParticipationRequestDto participationRequestDto);

    // -------------------- Вспомогательные методы --------------------
    @Named("mapEvent")
    default Event mapEvent(Long eventId) {
        if (eventId == null) return null;
        Event e = new Event();
        e.setId(eventId);
        return e;
    }

    @Named("mapUser")
    default User mapUser(Long userId) {
        if (userId == null) return null;
        User u = new User();
        u.setId(userId);
        return u;
    }

    default LocalDateTime mapCreated(LocalDateTime created) {
        return created == null ? null : created.truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
    }
}


