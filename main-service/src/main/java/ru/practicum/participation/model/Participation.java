package ru.practicum.participation.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.enums.PartState;

import java.time.LocalDateTime;

@Setter
@Getter
@Table(name = "participation")
@Entity
public class Participation {
    LocalDateTime created = LocalDateTime.now();
    Long event;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long requester;
    @Enumerated(EnumType.STRING)
    PartState status;
}
