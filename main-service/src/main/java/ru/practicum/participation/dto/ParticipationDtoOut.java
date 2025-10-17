package ru.practicum.participation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipationDtoOut {
    String created;
    Long event;
    Long id;
    Long requester;
    String status;
}
