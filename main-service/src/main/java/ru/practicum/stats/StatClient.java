package ru.practicum.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static ru.practicum.constants.DateTimeFormatConstants.FORMATTER;

@Slf4j
@Service
public class StatClient {
    private final WebClient webClient;

    @Autowired
    public StatClient(WebClient.Builder webClientBuilder, @Value("${client.url}") String gatewayUrl) {
        log.info("Инициализация StatClient с базовым URL: {}", gatewayUrl);

        this.webClient = webClientBuilder.baseUrl(gatewayUrl)
                .filter((request, next) -> {
                    String requestId = UUID.randomUUID().toString();
                    log.debug("[{}] --> {} {}", requestId, request.method(), request.url());
                    request.headers().forEach((name, values) ->
                            log.trace("[{}] --> Header: {}={}", requestId, name, String.join(",", values)));

                    long startTime = System.currentTimeMillis();

                    return next.exchange(request)
                            .flatMap(response -> {
                                long duration = System.currentTimeMillis() - startTime;
                                log.debug("[{}] <-- Статус ответа: {} ({} ms)", requestId, response.statusCode(), duration);

                                response.headers().asHttpHeaders().forEach((name, values) ->
                                        log.trace("[{}] <-- Response header: {}={}", requestId, name, String.join(",", values)));

                                if (response.statusCode().isError()) {
                                    log.warn("[{}] <-- Ошибка HTTP: {}", requestId, response.statusCode());
                                    return response.bodyToMono(String.class)
                                            .flatMap(body -> {
                                                log.warn("[{}] Тело ошибки: {}", requestId, body);
                                                return Mono.error(new ResponseStatusException(response.statusCode(), body));
                                            });
                                }

                                log.trace("[{}] Успешный ответ без ошибок", requestId);
                                return Mono.just(response);
                            });
                })
                .build();

        log.info("StatClient успешно инициализирован");
    }

    /**
     * Отправка события с конкретным ID
     */
    public void sendHitId(Long id, String app, String uri, String ip) {
        log.debug("Вызван sendHitId(id={}, app={}, uri={}, ip={})", id, app, uri, ip);
        sendHitInternal(id, app, uri, ip);
    }

    /**
     * Отправка события без ID
     */
    public void sendHit(String app, String uri, String ip) {
        log.debug("Вызван sendHit(app={}, uri={}, ip={})", app, uri, ip);
        sendHitInternal(null, app, uri, ip);
    }

    /**
     * Общий метод отправки события в StatsController
     */
    private void sendHitInternal(Long id, String app, String uri, String ip) {
        log.trace("Начало выполнения sendHitInternal(id={}, app={}, uri={}, ip={})", id, app, uri, ip);
        try {
            EndpointHitDto hit = new EndpointHitDto();
            hit.setId(id);
            hit.setApp(app);
            hit.setUri(uri);
            hit.setIp(ip);
            hit.setTimestamp(LocalDateTime.now());

            log.info("Отправка события в сервис статистики: {}", hit);
            long startTime = System.currentTimeMillis();

            webClient.post()
                    .uri("/hit")
                    .bodyValue(hit)
                    .retrieve()
                    .toBodilessEntity()
                    .doOnSuccess(v -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.info("Успешная отправка статистики для id={} ({} ms)", id, duration);
                    })
                    .doOnError(err -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.error("Ошибка при отправке статистики для id={} спустя {} ms: {}", id, duration, err.toString());
                    })
                    .block();

        } catch (Exception ex) {
            log.error("Ошибка отправки статистики для события id={}", id, ex);
        } finally {
            log.trace("Завершено выполнение sendHitInternal(id={})", id);
        }
    }

    /**
     * Получение статистики просмотров
     */
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", start.format(FORMATTER))
                        .queryParam("end", end.format(FORMATTER))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDto>>() {
                })
                .block();
    }
}





