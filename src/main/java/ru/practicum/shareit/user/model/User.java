package ru.practicum.shareit.user.model;


import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    // уникальный идентификатор пользователя
    @Positive
    private long id;
    // имя или логин пользователя
    private String name;
    // адрес электронной почты (учтите, что два пользователя не могут
    // иметь одинаковый адрес электронной почты)
    private String email;
}

