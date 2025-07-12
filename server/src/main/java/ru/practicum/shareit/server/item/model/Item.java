package ru.practicum.shareit.server.item.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.server.request.model.ItemRequest;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
public class Item {
    // уникальный идентификатор вещи
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    // краткое название
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    // развёрнутое описание
    @Column(name = "description", length = 512, nullable = false)
    private String description;
    // статус о том, доступна или нет вещь для аренды
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    // владелец вещи
    @Column(name = "owner_id", nullable = false)
    private long owner;
    // если вещь была создана по запросу другого пользователя, то в этом
    //поле будет храниться ссылка на соответствующий запрос
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

}