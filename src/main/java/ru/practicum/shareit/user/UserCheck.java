package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Objects;

public class UserCheck {

    // метод валидации при создании пользователя - поля должны быть все не null
    public static void isUserDto(UserDto userDto) {
        if (Objects.isNull(userDto.getName()) || Objects.isNull(userDto.getEmail()))
            throw new ValidationException(String.format("Пользователь %s не прошел валидацию при создании",
                    userDto));
    }

    // метод валидации поля имени и  email пользователя
    public static void isGetNameEmail(UserDto userDto) {
        if (Objects.nonNull(userDto.getName())
                && (userDto.getName().isBlank() || userDto.getName().isEmpty()))
            throw new ValidationException(String.format("Пользователь %s не прошел валидацию имени",
                    userDto));
        if (Objects.nonNull(userDto.getEmail())
                && (userDto.getEmail().isEmpty() || userDto.getEmail().isBlank()))
            throw new ValidationException(String.format("Пользователь %s не прошел валидацию почты",
                    userDto));
    }

    // метод валидации поля id пользователя
    public static void isGetId(long id) {
        if (id < 1) throw new ValidationException(
                String.format("ID %s пользователя не прошел валидацию", id));
    }

}
