package ru.practicum.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.enums.EveState;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "events")
@Entity
public class Event {
    @NotBlank
    private String annotation;
    @NotNull
    private Long category;
    @Column(name = "created_on")
    private LocalDateTime createdOn = LocalDateTime.now();
    private String description;
    @NotNull
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long initiator;
    @NotNull
    @Column(name = "location_lat")
    private Float locationLat;
    @NotNull
    @Column(name = "location_lon")
    private Float locationLon;
    @NotNull
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private EveState state;
    @NotBlank
    private String title;
}
