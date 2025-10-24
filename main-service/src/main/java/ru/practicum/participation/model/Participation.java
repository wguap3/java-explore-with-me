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
    private LocalDateTime created = LocalDateTime.now();
    private Long event;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long requester;
    @Enumerated(EnumType.STRING)
    private PartState status;
}
