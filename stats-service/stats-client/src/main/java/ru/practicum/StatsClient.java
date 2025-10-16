package ru.practicum;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StatsClient extends BaseClient {

    private static final String HIT_PATH = "/hit";
    private static final String STATS_PATH = "/stats";

    public StatsClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public void saveHit(EndpointHitDto hitDto) {
        post(HIT_PATH, hitDto);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", start.toString());
        params.put("end", end.toString());
        if (uris != null) params.put("uris", uris);
        params.put("unique", unique);

        return (List<ViewStatsDto>) get(STATS_PATH, params).getBody();
    }

    public boolean existsByIpAndUri(String ip, String uri) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(30);
        List<ViewStatsDto> stats = getStats(start, now, List.of(uri), true);

        if (stats == null || stats.isEmpty()) {
            return false;
        }

        return stats.stream()
                .anyMatch(s -> s.getUri().equals(uri) && s.getHits() > 0);
    }
}
