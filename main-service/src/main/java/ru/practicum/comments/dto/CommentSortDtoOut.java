package ru.practicum.comments.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentSortDtoOut {
    private Long id;
    private String text;
    private String eventAnnotation;
    private String creator;
    private String status;
}
