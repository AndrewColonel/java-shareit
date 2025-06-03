package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Booking {
    // уникальный идентификатор бронирования
    private long id;
    // дата и время начала бронирования
    private LocalDateTime start;
    //дата и время конца бронирования
    private LocalDateTime end;
    // вещь, которую пользователь бронирует
    private long item;
    //пользователь, который осуществляет бронирование
    private long booker;
    // статус бронирования. Может принимать одно из следующихзначений:
    // WAITING — новое бронирование, ожидает одобрения,
    // APPROVED — бронирование подтверждено владельцем,
    // REJECTED — бронирование отклонено владельцем,
    // CANCELED — бронирование отменено создателем.
    private Status status;
}
