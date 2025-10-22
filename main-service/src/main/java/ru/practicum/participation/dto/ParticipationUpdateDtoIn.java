package ru.practicum.participation.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.enums.UpdateState;

import java.util.List;

@Getter
@Setter
public class ParticipationUpdateDtoIn {
    List<Long> requestIds;
    UpdateState status;
}
