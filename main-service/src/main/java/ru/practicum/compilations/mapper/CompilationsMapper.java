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

    // MapStruct сгенерирует реализацию для этого метода
    @Mapping(target = "pinned", expression = "java(compilationsDtoIn.getPinned() != null ? compilationsDtoIn.getPinned() : false)")
    @Mapping(target = "title", source = "title")
    public abstract Compilations mapCompilationsDtoInToCompilations(CompilationsDtoIn compilationsDtoIn);

    // default метод с телом, может использовать jdbcTemplate и eventService
    public CompilationsDtoOut mapCompilationsToCompilationsDtoOut(Compilations compilations) {
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
