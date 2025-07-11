package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStateRequestDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(long userId, NewBookingDto newBookingDto);

    BookingDto updateBooking(long userId, long bookingId, boolean approved);

    BookingDto getBookingById(long userId, long bookingId);

    Collection<BookingStateRequestDto> findAllBookings(long userId, String state);

    Collection<BookingStateRequestDto> findAllOwnerBooking(long userId, String state);
}
