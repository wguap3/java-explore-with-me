package ru.practicum;


import io.micrometer.common.lang.Nullable;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        HttpEntity<T> request = new HttpEntity<>(body, defaultHeaders());
        try {
            return rest.exchange(path, HttpMethod.POST, request, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        HttpEntity<Void> request = new HttpEntity<>(defaultHeaders());
        try {
            if (parameters != null) {
                return rest.exchange(path, HttpMethod.GET, request, Object.class, parameters);
            }
            return rest.exchange(path, HttpMethod.GET, request, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}