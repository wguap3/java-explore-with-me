package ru.practicum.participation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ParticipationUpdateDtoOut {
    private List<ParticipationDtoOut> confirmedRequests = new ArrayList<>();
    private List<ParticipationDtoOut> rejectedRequests = new ArrayList<>();
}
