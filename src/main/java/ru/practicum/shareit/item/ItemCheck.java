package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    // метод валидации владельца как зарегистрированного пользователя
    public static void isUser(long userId, Collection<Long> users) {
        // проверяю - содержится ли id пользователя владельца в ключах мапы владельцев
        if (!users.contains(userId))
            throw new NotFoundException(
                    String.format("пользователю ID %s не зарегистрирован", userId));
    }

    // метод валидации пользователя как владельца конкретной вещи
    public static void isOwner(long userId, long itemId, Map<Long, Set<Long>> owners) {
        // проверяю, содержится ли id пользователя владельца в ключах мапы владельцев
        // т.е. является ли данный пользователь - владельцем
        if (!owners.containsKey(userId))
            throw new NotFoundException(
                    String.format("пользователю ID %s не является владельцем", userId));
        // проверяю содержится ли id вещи во множестве вещей данного пользователя-владельца
        if (!owners.get(userId).contains(itemId))
            throw new NotFoundException(
                    String.format("Вещь с ID %s, принадлежащая пользователю ID %s не найдена",
                            itemId, userId));
    }

    // метод валидации строки запроса
    public static void isStringQuery(String searchQuery) {
        if (Objects.isNull(searchQuery))
            throw new ValidationException(String.format("Запрос %s не прошел валидацию", searchQuery));
    }
}
