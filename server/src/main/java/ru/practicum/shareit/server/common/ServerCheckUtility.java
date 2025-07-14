package ru.practicum.shareit.server.common;

import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.model.Item;

public class ServerCheckUtility {

    //ITEMS
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

    // метод валидации доступности вещи
    public static void isItemAvailable(Item item) {
        if (item.getAvailable().equals(false))
            throw new ValidationException(String.format("Вещь %s для заказа недоступна", item.getId()));
    }

    //USERS
    // метод валидации автора и влдальца
    public static void isBookerOrOwner(long userId, long bookerId, long ownerId) {
        if ((userId != bookerId) && (userId != ownerId))
            throw new ValidationException(
                    String.format("Пользователь с ID %s не является ни автором бронирования (ID %s), " +
                                    "ни владельцем вещи (ID %s)",
                            userId, bookerId, ownerId));
    }

}
