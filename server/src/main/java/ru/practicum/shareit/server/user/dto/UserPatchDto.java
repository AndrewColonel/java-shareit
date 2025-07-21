package ru.practicum.shareit.server.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPatchDto {
    // имя или логин пользователя
    private String name;
    // адрес электронной почты (учтите, что два пользователя не могут
    // иметь одинаковый адрес электронной почты)
    private String email;
}
