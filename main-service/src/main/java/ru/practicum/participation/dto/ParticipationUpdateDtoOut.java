package ru.practicum.participation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ParticipationUpdateDtoOut {
    List<ParticipationDtoOut> confirmedRequests;
    List<ParticipationDtoOut> rejectedRequests;
}
