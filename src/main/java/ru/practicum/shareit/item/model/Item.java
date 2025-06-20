package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.request.model.ItemRequest;

@Entity
@Table(name = "items")
@Getter @Setter @ToString
public class Item {
    // уникальный идентификатор вещи
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    // краткое название
    @Column(name = "name")
    private String name;
    // развёрнутое описание
    @Column(name = "description")
    private String description;
    // статус о том, доступна или нет вещь для аренды
    @Column(name = "is_available")
    private Boolean available;
    // владелец вещи
    @Column(name = "owner_id")
    private long owner;
    // если вещь была создана по запросу другого пользователя, то в этом
    //поле будет храниться ссылка на соответствующий запрос
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

}