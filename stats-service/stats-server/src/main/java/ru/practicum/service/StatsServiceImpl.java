package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.BadRequest;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.EndpointHitEntity;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;
    private final StatsMapper mapper;

    @Override
    public void saveHit(EndpointHitDto hitDto) {
        EndpointHitEntity entity = mapper.toEndpointHitEntity(hitDto);
        repository.save(entity);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {
        if (uris == null) {
            uris = Collections.emptyList();
        }
        if (end.isBefore(start)) {
            throw new BadRequest("Дата начала позже, чем дата конца!");
        }
        if (unique) {
            return repository.getUniqueStats(start, end, uris);
        } else {
            return repository.getStats(start, end, uris);
        }
    }
}
