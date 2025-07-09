package ru.practicum.shareit.request.model;
// класс, отвечающий за запрос вещи

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
public class ItemRequest {
    // уникальный идентификатор запроса
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    // текст запроса, содержащий описание требуемой вещи
    @Column(name = "description", length = 512, nullable = false)
    private String description;
    // пользователь, создавший запрос
    @Column(name = "requestor_id", nullable = false)
    private long requestor;
    // дата и время создания запроса
    @Column(name = "created", nullable = false)
    private LocalDateTime created = LocalDateTime.now();
    @Transient
    private List<ItemAnswer> items;

}
