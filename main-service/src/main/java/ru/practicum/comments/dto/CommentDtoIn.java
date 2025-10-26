package ru.practicum.comments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CommentDtoIn {
    @NotBlank
    private String text;
}
