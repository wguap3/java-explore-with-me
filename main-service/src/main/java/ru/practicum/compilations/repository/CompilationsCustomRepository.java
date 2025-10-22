package ru.practicum.compilations.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CompilationsCustomRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveCompilationEventsBatch(Long compilationId, List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) return;

        List<Object[]> batchArgs = eventIds.stream()
                .map(eventId -> new Object[]{compilationId, eventId})
                .toList();

        jdbcTemplate.batchUpdate(
                "INSERT INTO compilations_events (compilation_id, event_id) VALUES (?, ?)",
                batchArgs
        );
    }
}

