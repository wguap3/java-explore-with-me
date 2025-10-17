package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import ru.practicum.dto.EndpointHitDto;
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
    public Mono<Void> sendHit(String app, String uri, String ip) {
        EndpointHitDto hit = new EndpointHitDto();
        hit.setApp(app);
        hit.setUri(uri);
        hit.setIp(ip);
        hit.setTimestamp(LocalDateTime.now());

        log.info("Отправка события в сервис статистики: {}", hit);

        return webClient.post()
                .uri("/hit")
                .bodyValue(hit)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(resp -> log.info("Событие отправлено"))
                .doOnError(err -> log.error("Ошибка отправки события", err))
                .then();
    }


    /**
     * Отправка хита с конкретным ID
     */
    public Mono<Void> sendHitId(Long id, String app, String uri, String ip) {
        EndpointHitDto hit = new EndpointHitDto();
        hit.setApp(app);
        hit.setUri(uri);
        hit.setIp(ip);
        hit.setTimestamp(LocalDateTime.now());

        log.info("Отправка события с ID={} в сервис статистики: {}", id, hit);

        // Отправка POST запроса на сервер статистики
        return webClient.post()
                .uri("/hit/{id}", id)  // можно указать id в URI, если ваш сервер это поддерживает
                .bodyValue(hit)        // обязательно тело запроса
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(resp -> log.info("Событие с ID={} отправлено успешно", id))
                .doOnError(err -> log.error("Ошибка отправки события с ID={}", id, err))
                .then();          // асинхронный вызов
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


