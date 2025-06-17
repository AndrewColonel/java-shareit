package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(long userId, BookingDto bookingDto);

    BookingDto updateBooking(long userId, long bookingId, boolean approved);

    BookingDto getBookingById(long userId, long bookingId);

    Collection<BookingDto> findAllBookings(long userId, String state);

    Collection<BookingDto> findAllOwnerBooking(long userId, String state);
}
