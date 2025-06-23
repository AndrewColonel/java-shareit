package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewUserDto {
    // уникальный идентификатор пользователя
    private long id;
    // имя или логин пользователя
    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    // адрес электронной почты (учтите, что два пользователя не могут
    // иметь одинаковый адрес электронной почты)
    @Email
    @NotNull
    @Size(min = 1, max = 255)
    private String email;
}
