package ru.practicum.shareit.gateway.common;



import jakarta.validation.ValidationException;
import ru.practicum.shareit.gateway.booking.dto.NewBookingDto;
import ru.practicum.shareit.gateway.item.dto.ItemPatchDto;
import ru.practicum.shareit.gateway.user.dto.UserPatchDto;

import java.util.Objects;

public class GatewayCheckUtility {

    //ITEMS
    // метод валидации поля id пользователя
    public static void isItemGetId(long id) {
        if (id < 1) throw new ValidationException(
                String.format("ID %s  не прошел валидацию", id));
    }

    // метод  валидации полей itemPatchDto
    public static void isItemPatchDto(ItemPatchDto itemPatchDto) {
        if (Objects.nonNull(itemPatchDto.getName())
                && (itemPatchDto.getName().isBlank() || itemPatchDto.getName().isEmpty()))
            throw new ValidationException(
                    String.format("Вещь %s не прошла валидацию имени при обновлении",
                            itemPatchDto));
        if (Objects.nonNull(itemPatchDto.getDescription())
                && (itemPatchDto.getDescription().isEmpty() || itemPatchDto.getDescription().isBlank()))
            throw new ValidationException(
                    String.format("Вещь %s не прошла валидацию описания при обновлении", itemPatchDto));
    }

    // метод валидации пользователя как владельца конкретной вещи
    public static void isOwner(long userId, long ownerId) {
        if (userId != ownerId)
            throw new ValidationException(
                    String.format("Пользователь с ID %s не является владельцем вещи c ID влдаельца %s",
                            userId, ownerId));
    }

    // метод валидации пользователя как владельца конкретной вещи
    public static boolean isOwnerBoolean(long userId, long ownerId) {
       return userId == ownerId;
    }

    // метод валидации строки запроса
    public static void isStringQuery(String searchQuery) {
        if (Objects.isNull(searchQuery))
            throw new ValidationException(String.format("Запрос %s не прошел валидацию", searchQuery));
    }


    //USERS
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
    public static void isUserGetId(long id) {
        if (id < 1) throw new ValidationException(
                String.format("ID %s пользователя не прошел валидацию", id));
    }

    //BOOKINGS
    // метод валидации BookingDto
    public static void isStartEndValid(NewBookingDto newBookingDto) {
        if (newBookingDto.getStart().isAfter(newBookingDto.getEnd())
                || newBookingDto.getStart().isEqual(newBookingDto.getEnd()))
            throw new ValidationException(String.format("Неверно указаны даты аренды." +
                            "Дата начала аренды %s наступает после или одновременно с датой конца %s аренды",
                    newBookingDto.getStart(), newBookingDto.getEnd()));

    }

    // метод валидации автора бронирования
    public static void isBooker(long userId, long bookerId) {
        if (userId != bookerId)
            throw new ValidationException(
                    String.format("Пользователь с ID %s не является автором бронирования с ID %s",
                            userId, bookerId));
    }

    // метод валидации автора и влдальца
    public static void isBookerOrOwner(long userId, long bookerId, long ownerId) {
        if ((userId != bookerId) && (userId != ownerId))
            throw new ValidationException(
                    String.format("Пользователь с ID %s не является ни автором бронирования (ID %s), " +
                                    "ни владельцем вещи (ID %s)",
                            userId, bookerId, ownerId));
    }

}
