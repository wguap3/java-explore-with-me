package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserDtoIn {
    @Email
    @NotBlank
    @Length(min = 6, max = 254)
    String email;
    @NotBlank
    @Length(min = 2, max = 250)
    String name;
}
