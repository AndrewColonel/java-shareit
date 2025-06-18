package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.Objects;

public class ItemCheck {

    // метод валидации поля id пользователя
    public static void isGetId(long id) {
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

    // метод валидации строки запроса
    public static void isStringQuery(String searchQuery) {
        if (Objects.isNull(searchQuery))
            throw new ValidationException(String.format("Запрос %s не прошел валидацию", searchQuery));
    }

    // метод валидации доступности вещи
    public static void isItemAvailable(Item item) {
        if (item.getAvailable().equals(false))
            throw new ValidationException(String.format("Вещь %s для заказа недоступна", item.getId()));
    }

}
