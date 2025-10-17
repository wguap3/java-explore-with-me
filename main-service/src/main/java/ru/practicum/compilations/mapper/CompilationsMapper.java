package ru.practicum.compilations.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.compilations.dto.CompilationsDtoIn;
import ru.practicum.compilations.dto.CompilationsDtoOut;
import ru.practicum.compilations.model.Compilations;
import ru.practicum.event.service.EventService;


import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationsMapper {

    @Autowired
    EventService eventService = null;

    @Autowired
    JdbcTemplate jdbcTemplate = null;

    @Mapping(target = "pinned", expression = "java(compilationsDtoIn.getPinned() != null ? compilationsDtoIn.getPinned() : false)")
    @Mapping(target = "title", source = "title")
    Compilations mapCompilationsDtoInToCompilations(CompilationsDtoIn compilationsDtoIn);

    default CompilationsDtoOut mapCompilationsToCompilationsDtoOut(Compilations compilations) {
        CompilationsDtoOut dto = new CompilationsDtoOut();

        List<Long> eventIds = jdbcTemplate.query(
                "SELECT ce.event_id FROM compilations_events AS ce WHERE ce.compilation_id = ?",
                (rs, rowNum) -> rs.getLong("event_id"),
                compilations.getId()
        );

        dto.setEvents(eventService.getCompilationsEvents(eventIds));
        dto.setId(compilations.getId());
        dto.setPinned(compilations.getPinned());
        dto.setTitle(compilations.getTitle());

        return dto;
    }
}
