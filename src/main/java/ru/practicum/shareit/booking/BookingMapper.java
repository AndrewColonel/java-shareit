package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static ru.practicum.shareit.item.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking toBooking(NewBookingDto newBookingDto) {
        Booking booking = new Booking();
        booking.setStart(newBookingDto.getStart());
        booking.setEnd(newBookingDto.getEnd());
        booking.setItem(newBookingDto.getItemId());
        booking.setBooker(newBookingDto.getBooker());
        booking.setStatus(newBookingDto.getStatus());
        return booking;
    }

    public static BookingDto toBookingDto(Booking booking, User user, Item item) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(toItemDto(item))
                .booker(toUserDto(user))
                .status(booking.getStatus())
                .build();
    }


}
