package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatClient {

    private final WebClient webClient;

    /**
     * Отправка события с конкретным ID
     */
    public void sendHitId(Long id, String app, String uri, String ip) {
        sendHitInternal(id, app, uri, ip);
    }

    /**
     * Отправка события без ID
     */
    public void sendHit(String app, String uri, String ip) {
        sendHitInternal(null, app, uri, ip);
    }

    /**
     * Общий метод отправки события в StatsController
     */
    private void sendHitInternal(Long id, String app, String uri, String ip) {
        try {
            EndpointHitDto hit = new EndpointHitDto();
            hit.setId(id);
            hit.setApp(app);
            hit.setUri(uri);
            hit.setIp(ip);
            hit.setTimestamp(LocalDateTime.now());

            log.info("Отправка события в сервис статистики: {}", hit);

            webClient.post()
                    .uri("/hit")
                    .bodyValue(hit)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        } catch (Exception ex) {
            log.error("Ошибка отправки статистики для события id={}", id, ex);
        }
    }

    /**
     * Получение статистики просмотров
     */
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/stats")
                    .queryParam("start", start)
                    .queryParam("end", end)
                    .queryParam("unique", unique);

            if (uris != null) {
                for (String u : uris) {
                    builder.queryParam("uris", u);
                }
            }

            String uri = builder.build().toUriString();

            log.info("Запрос статистики по URI: {}", uri);

            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDto>>() {
                    })
                    .block();

        } catch (Exception ex) {
            log.error("Ошибка получения статистики", ex);
            return Collections.emptyList();
        }
    }
}



