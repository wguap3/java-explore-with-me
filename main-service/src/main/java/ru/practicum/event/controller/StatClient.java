package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class StatClient {

    private final WebClient webClient;

    public StatClient(WebClient.Builder webClientBuilder,
                      @Value("${stats-service.url}") String statsServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(statsServiceUrl)
                .build();
    }

    /**
     * Отправка хита без ID
     */
    public void sendHit() {
        log.info("Отправка события в сервис статистики (без ID)");
        webClient.post()
                .uri("/hit")
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(resp -> log.info("Событие отправлено"))
                .doOnError(err -> log.error("Ошибка отправки события", err))
                .subscribe();  // Асинхронный вызов
    }

    /**
     * Отправка хита с конкретным ID
     */
    public void sendHitId(Long id) {
        log.info("Отправка события с ID={} в сервис статистики", id);

        webClient.post()
                .uri("/hit/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(resp -> log.info("Событие с ID={} отправлено успешно", id))
                .doOnError(e -> log.error("Ошибка отправки события с ID={}", id, e))
                .subscribe();
    }

    /**
     * Получение статистики
     */
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, boolean unique) {
        log.info("Запрос статистики: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        return webClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end)
                            .queryParam("unique", unique);
                    if (uris != null && !uris.isEmpty()) {
                        uris.forEach(uri -> builder.queryParam("uris", uri));
                    }
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDto>>() {
                })
                .block();
    }
}


