package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatClient statClient;

    public void sendHit(String app, String uri, String ip) {
        try {
            statClient.sendHit(app, uri, ip);
        } catch (Exception ex) {
            log.error("Ошибка отправки статистики для uri={}", uri, ex);
        }
    }

    public void sendHitId(Long id, String app, String uri, String ip) {
        try {
            statClient.sendHitId(id, app, uri, ip);
        } catch (Exception ex) {
            log.error("Ошибка отправки статистики для id={}", id, ex);
        }
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        try {
            return statClient.getStats(start, end, uris, unique);
        } catch (Exception ex) {
            log.error("Ошибка получения  статистики для uris={}", uris);
            return Collections.emptyList();
        }
    }
}
