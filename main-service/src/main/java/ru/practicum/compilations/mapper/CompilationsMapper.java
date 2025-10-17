package ru.practicum.compilations.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.compilations.dto.CompilationsDtoIn;
import ru.practicum.compilations.dto.CompilationsDtoOut;
import ru.practicum.compilations.model.Compilations;
import ru.practicum.event.service.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompilationsMapper {
    private final EventService eventService;
    private final JdbcTemplate jdbcTemplate;

    public Compilations mapCompilationsDtoInToCompilations(CompilationsDtoIn compilationsDtoIn) {
        Compilations compilations = new Compilations();
        if (compilationsDtoIn.getPinned() == null) {
            compilations.setPinned(false);
        } else {
            compilations.setPinned(compilationsDtoIn.getPinned());
        }
        compilations.setTitle(compilationsDtoIn.getTitle());
        return compilations;
    }

    public CompilationsDtoOut mapCompilationsToCompilationsDtoOut(Compilations compilations) {
        CompilationsDtoOut compilationsDtoOut = new CompilationsDtoOut();
        List<Long> eventIds = jdbcTemplate.query("SELECT ce.event_id FROM compilations_events AS ce WHERE ce.compilation_id = ?", (rs, rowNum) -> rs.getLong("event_id"), compilations.getId());
        compilationsDtoOut.setEvents(eventService.getCompilationsEvents(eventIds));
        compilationsDtoOut.setId(compilations.getId());
        compilationsDtoOut.setPinned(compilations.getPinned());
        compilationsDtoOut.setTitle(compilations.getTitle());
        return compilationsDtoOut;
    }
}
