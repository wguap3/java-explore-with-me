package ru.practicum.participation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipationDtoOut {
    private String created;
    private Long event;
    private Long id;
    private Long requester;
    private String status;
}
