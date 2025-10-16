package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 250, message = "Имя должно быть от 2 до 250 символов")
    private String name;
    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    @Size(min = 6, max = 254, message = "Email слишком длинный")
    private String email;
}
