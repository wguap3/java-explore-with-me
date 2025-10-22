package ru.practicum.location;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Location {
    @NotNull
    Float lat;
    @NotNull
    Float lon;
}
