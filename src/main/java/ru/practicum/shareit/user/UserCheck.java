package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.util.Objects;

public class UserCheck {

    // метод валидации при создании пользователя - поля должны быть все не null
    // метод более не воссстребован - проверка аннотациями, введена модель UserPatchDto
    public static void isUserDto(UserDto userDto) {
        if (Objects.isNull(userDto.getName()) || Objects.isNull(userDto.getEmail()))
            throw new ValidationException(String.format("Пользователь %s не прошел валидацию при создании",
                    userDto));
    }

    // метод валидации поля имени и  email пользователя
    public static void isUserPatchDto(UserPatchDto userPatchDto) {
        if (Objects.nonNull(userPatchDto.getName())
                && (userPatchDto.getName().isBlank() || userPatchDto.getName().isEmpty()))
            throw new ValidationException(String.format("Пользователь %s не прошел валидацию имени",
                    userPatchDto));
        if (Objects.nonNull(userPatchDto.getEmail())
                && (userPatchDto.getEmail().isEmpty() || userPatchDto.getEmail().isBlank()))
            throw new ValidationException(String.format("Пользователь %s не прошел валидацию почты",
                    userPatchDto));
    }

    // метод валидации поля id пользователя
    // метод невостребован - id передается через PAthVariable - нет значения, не работает ендпоинт
    // проверка на положительное значение через анногтацию @Positive
    public static void isGetId(long id) {
        if (id < 1) throw new ValidationException(
                String.format("ID %s пользователя не прошел валидацию", id));
    }

}
