package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                // при создании пользователя валидация данных полей будет выполнена в сервисе
                // однако при обновлении методолм Patch поля могут быть null в любой комбинации
                .name(Objects.nonNull(userDto.getName()) ?
                        userDto.getName() : null)
                .email(Objects.nonNull(userDto.getEmail()) ?
                        userDto.getEmail() : null)
                .build();
    }

}
