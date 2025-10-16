package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {
    @NotBlank
    @Size(min = 20, max = 2000, message = "Description must be at least 20 characters")
    private String annotation;
    private Long category;
    @NotBlank
    @Size(min = 20, max = 7000, message = "Description must be at least 20 characters")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private LocationDto location;
    @NotNull
    private Boolean paid;
    @PositiveOrZero(message = "Participant limit must be zero or positive")
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotBlank
    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    private String title;

    private StateActionAdmin stateAction;

    public enum StateActionAdmin {
        PUBLISH_EVENT,
        REJECT_EVENT
    }
}

