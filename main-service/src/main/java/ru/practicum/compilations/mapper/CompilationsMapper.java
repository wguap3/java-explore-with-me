package ru.practicum.compilations.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.compilations.dto.CompilationsDtoIn;
import ru.practicum.compilations.dto.CompilationsDtoOut;
import ru.practicum.compilations.model.Compilations;
import ru.practicum.event.service.EventService;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CompilationsMapper {

    @Autowired
    protected EventService eventService;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Mapping(target = "pinned", expression = "java(compilationsDtoIn.getPinned() == null ? false : compilationsDtoIn.getPinned())")
    @Mapping(target = "title", source = "compilationsDtoIn.title")
    public abstract Compilations mapCompilationsDtoInToCompilations(CompilationsDtoIn compilationsDtoIn);

    @Mapping(target = "events", expression = "java(eventService.getCompilationsEvents(getEventIds(compilations.getId())))")
    @Mapping(target = "id", source = "compilations.id")
    @Mapping(target = "pinned", source = "compilations.pinned")
    @Mapping(target = "title", source = "compilations.title")
    public abstract CompilationsDtoOut mapCompilationsToCompilationsDtoOut(Compilations compilations);

    protected List<Long> getEventIds(Long compilationId) {
        return jdbcTemplate.query(
                "SELECT ce.event_id FROM compilations_events AS ce WHERE ce.compilation_id = ?",
                (rs, rowNum) -> rs.getLong("event_id"),
                compilationId
        );
    }
}




