package ru.practicum.shareit.gateway.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

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
}
