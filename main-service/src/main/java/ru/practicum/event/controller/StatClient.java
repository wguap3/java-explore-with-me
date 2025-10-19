package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.practicum.dto.ViewStatsDto;

import java.util.List;

@Service
@Slf4j
public class StatClient {

    private final WebClient webClient;

    public StatClient(WebClient.Builder webClientBuilder, @Value("${client.url}") String gatewayUrl) {
        this.webClient = webClientBuilder.baseUrl(gatewayUrl)
                .filter((request, next) -> next.exchange(request)
                        .flatMap(response -> {
                            if (response.statusCode().isError()) {
                                return response.bodyToMono(String.class)
                                        .flatMap(body -> {
                                            return Mono.error(new ResponseStatusException(response.statusCode(), body));
                                        });
                            }
                            return Mono.just(response);
                        }))
                .build();
    }

    public void sendHitId(Long id) {
        log.info("Отправка данных о событии с ID {} в гейтвей статистики", id);
        webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/events/{id}").build(id))
                .exchangeToMono(response -> response.toEntity(Void.class))
                .block();
    }

    public void sendHit() {
        log.info("Отправка данных о событии в гейтвей статистики");
        webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/events").build())
                .exchangeToMono(response -> response.toEntity(Void.class))
                .block();
    }

    public List<ViewStatsDto> getHits(String start, String end, String[] uris, Boolean unique) {
        log.info("Запрос данных о событии из гейтвея статистики {}, {} , {} ,{}", start, end, uris, unique);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/events/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDto>>() {
                })
                .block();
    }
}


