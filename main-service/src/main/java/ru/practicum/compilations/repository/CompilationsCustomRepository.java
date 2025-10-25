package ru.practicum.compilations.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Long> getEventIdsByCompilationId(Long compilationId) {
        return jdbcTemplate.query(
                "SELECT event_id FROM compilations_events WHERE compilation_id = ?",
                (rs, rowNum) -> rs.getLong("event_id"),
                compilationId
        );
    }

    @Transactional
    public void updateCompilationEvents(Long compilationId, List<Long> eventIds) {
        jdbcTemplate.update("DELETE FROM compilations_events WHERE compilation_id = ?", compilationId);

        for (Long eventId : eventIds) {
            jdbcTemplate.update(
                    "INSERT INTO compilations_events (compilation_id, event_id) VALUES (?, ?)",
                    compilationId, eventId
            );
        }
    }


}

