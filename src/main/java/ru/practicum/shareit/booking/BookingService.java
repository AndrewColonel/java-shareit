package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOwnerRequestDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(long userId, NewBookingDto newBookingDto);

    BookingDto updateBooking(long userId, long bookingId, boolean approved);

    BookingDto getBookingById(long userId, long bookingId);

    Collection<BookingOwnerRequestDto> findAllBookings(long userId, State state);

    Collection<BookingOwnerRequestDto> findAllOwnerBooking(long userId, State state);
}
