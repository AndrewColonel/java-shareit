package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString
public class Booking {
    // уникальный идентификатор бронирования
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    // дата и время начала бронирования
    @Column(name = "start_date")
    private LocalDateTime start;
    //дата и время конца бронирования
    @Column(name = "end_date")
    private LocalDateTime end;
    // вещь, которую пользователь бронирует
    @Column(name = "item_id")
    private long item;
    //пользователь, который осуществляет бронирование
    @Column(name = "booker_id")
    private long booker;
    // статус бронирования. Может принимать одно из следующихзначений:
    // WAITING — новое бронирование, ожидает одобрения,
    // APPROVED — бронирование подтверждено владельцем,
    // REJECTED — бронирование отклонено владельцем,
    // CANCELED — бронирование отменено создателем.
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15)
    private Status status;
}
