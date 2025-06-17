package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

public class BookingCheck {

    // метод валидации BookingDto
    public static void isStartEndValid(BookingDto bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().isEqual(bookingDto.getEnd()))
            throw new ValidationException(String.format("Неверно указаны даты аренды." +
                    "Дата начала аренды %s наступает после даты конца %s аренды",
                    bookingDto.getStart(), bookingDto.getEnd()));

    }

    // метод валидации автора бронирования
    public static void isBooker(long userId, long bookerId) {
        if (userId != bookerId)
            throw new NotFoundException(
                    String.format("Пользователь с ID %s не является автором бронирования с ID %s",
                            userId, bookerId));
    }

}
