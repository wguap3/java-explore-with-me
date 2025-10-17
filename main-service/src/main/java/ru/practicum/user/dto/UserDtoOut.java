package ru.practicum.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Data
public class UserDtoOut {
    String email;
    Long id;
    String name;
}
