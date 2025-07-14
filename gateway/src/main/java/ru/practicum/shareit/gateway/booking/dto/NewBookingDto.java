package ru.practicum.shareit.gateway.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewBookingDto {
    // дата и время начала бронирования
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    //дата и время конца бронирования
    @NotNull
    @Future
    private LocalDateTime end;
    // вещь, которую пользователь бронирует
    @Positive
    private long itemId;
}
