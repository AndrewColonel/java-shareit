package ru.practicum.shareit.server.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NewBookingDto {
    // дата и время начала бронирования
    private LocalDateTime start;
    //дата и время конца бронирования
    private LocalDateTime end;
    // вещь, которую пользователь бронирует
    private long itemId;
}
