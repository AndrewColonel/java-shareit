package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
