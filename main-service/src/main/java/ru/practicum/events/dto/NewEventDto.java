package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    private String title;
    @NotBlank
    @Size(min = 20, max = 2000, message = "Description must be at least 20 characters")
    private String annotation;
    @NotBlank
    @Size(min = 20, message = "Description must be at least 20 characters")
    private String description;
    @NotNull
    private Long category;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private LocalDateTime eventDate;
    @NotNull
    private Boolean paid;
    @PositiveOrZero(message = "Participant limit must be zero or positive")
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotNull
    private LocationDto location;
}


