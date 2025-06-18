package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.exception.ValidationException;

public class BookingCheck {

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
                    String.format("Пользователь с ID %s не является ни автором бронирования с ID %s, " +
                                    "ни владельцем вещи c ID влдаельца %s",
                            userId, bookerId, ownerId));
    }

}
