package ru.practicum.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.enums.EveState;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "events")
@Entity
@ToString
public class Event {
    @NotBlank
    String annotation;
    @NotNull
    Long category;
    @Column(name = "created_on")
    LocalDateTime createdOn = LocalDateTime.now();
    String description;
    @NotNull
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotNull
    Long initiator;
    @NotNull
    @Column(name = "location_lat")
    Float locationLat;
    @NotNull
    @Column(name = "location_lon")
    Float locationLon;
    @NotNull
    Boolean paid;
    @Column(name = "participant_limit")
    Integer participantLimit;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    EveState state;
    @NotBlank
    String title;
}
