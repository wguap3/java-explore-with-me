package ru.practicum.participation.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.enums.PartState;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
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
