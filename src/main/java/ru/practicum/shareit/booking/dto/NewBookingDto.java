package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Data
@Builder
public class NewBookingDto {
    // дата и время начала бронирования
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    //дата и время конца бронирования
    @NotNull
    @FutureOrPresent
    private LocalDateTime end;
    // вещь, которую пользователь бронирует
    @Positive
    private long itemId;
    //пользователь, который осуществляет бронирование
    private long booker;
    // статус бронирования. Может принимать одно из следующихзначений:
    // WAITING — новое бронирование, ожидает одобрения,
    // APPROVED — бронирование подтверждено владельцем,
    // REJECTED — бронирование отклонено владельцем,
    // CANCELED — бронирование отменено создателем.
    private Status status;
}
