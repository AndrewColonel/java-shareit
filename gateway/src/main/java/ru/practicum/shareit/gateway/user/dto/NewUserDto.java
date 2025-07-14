package ru.practicum.shareit.gateway.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewUserDto {
    // уникальный идентификатор пользователя
    private long id;
    // имя или логин пользователя
    @NotBlank
    private String name;
    // адрес электронной почты (учтите, что два пользователя не могут
    // иметь одинаковый адрес электронной почты)
    @Email
    @NotBlank
    private String email;
}
