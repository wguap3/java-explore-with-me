package ru.practicum.participation.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.enums.UpdateState;

import java.util.List;

@Data
public class ParticipationUpdateDtoIn {
    List<Long> requestIds;
    UpdateState status;
}
