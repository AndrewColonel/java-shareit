package ru.practicum.shareit.request.model;
// класс, отвечающий за запрос вещи

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter @Setter @ToString
public class ItemRequest {
    // уникальный идентификатор запроса
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    // текст запроса, содержащий описание требуемой вещи
    @Column(name = "description")
    private String description;
    // пользователь, создавший запрос
    @Column(name = "requestor_id")
    private long requestor;
    // дата и время создания запроса
    @Transient
    private LocalDateTime created;
}
