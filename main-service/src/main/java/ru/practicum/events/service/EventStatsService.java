package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventStatsService {

    private final StatsClient statsClient;

    public void registerView(String uri, String ip) {
        EndpointHitDto hit = new EndpointHitDto();
        hit.setApp("explore-with-me");
        hit.setUri(uri);
        hit.setIp(ip);
        hit.setTimestamp(LocalDateTime.now());

        statsClient.saveHit(hit);
    }


    public Map<Long, Long> getViews(List<Long> eventIds) {
        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .toList();

        List<ViewStatsDto> stats = statsClient.getStats(
                LocalDateTime.now().minusYears(10),
                LocalDateTime.now(),
                uris,
                true
        );

        Map<Long, Long> viewsMap = new HashMap<>();
        for (ViewStatsDto dto : stats) {
            Long id = Long.parseLong(dto.getUri().split("/")[2]);
            viewsMap.put(id, dto.getHits());
        }
        return viewsMap;
    }


}
