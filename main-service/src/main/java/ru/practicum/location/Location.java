package ru.practicum.location;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Location {
    @NotNull
    Float lat;
    @NotNull
    Float lon;
}
