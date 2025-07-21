package ru.practicum.shareit.server.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.server.booking.Status;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    // уникальный идентификатор бронирования
    private long id;
    // дата и время начала бронирования
    private LocalDateTime start;
    //дата и время конца бронирования
    private LocalDateTime end;
    // вещь, которую пользователь бронирует
    private ItemDto item;
    //пользователь, который осуществляет бронирование
    private UserDto booker;
    // статус бронирования. Может принимать одно из следующихзначений:
    // WAITING — новое бронирование, ожидает одобрения,
    // APPROVED — бронирование подтверждено владельцем,
    // REJECTED — бронирование отклонено владельцем,
    // CANCELED — бронирование отменено создателем.
    private Status status;
}
