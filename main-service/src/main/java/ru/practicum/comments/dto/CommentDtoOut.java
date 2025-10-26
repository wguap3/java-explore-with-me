package ru.practicum.comments.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.dto.EventShortDtoOut;
import ru.practicum.user.dto.UserShortDtoOut;

@Getter
@Setter
public class CommentDtoOut {
    private Long id;
    private EventShortDtoOut eventShortDtoOut;
    private String text;
    private UserShortDtoOut userShortDtoOut;
    private String status;
}
