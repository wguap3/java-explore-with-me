package ru.practicum.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDtoOut {
    private String email;
    private Long id;
    private String name;
}
