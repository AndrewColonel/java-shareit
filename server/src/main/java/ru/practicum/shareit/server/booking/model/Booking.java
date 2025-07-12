package ru.practicum.shareit.server.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.server.booking.Status;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

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
    //пользователь, который осуществляет бронирование
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;
    // вещь, которую пользователь бронирует
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    // статус бронирования. Может принимать одно из следующихзначений:
    // WAITING — новое бронирование, ожидает одобрения,
    // APPROVED — бронирование подтверждено владельцем,
    // REJECTED — бронирование отклонено владельцем,
    // CANCELED — бронирование отменено создателем.
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15)
    private Status status;

}
