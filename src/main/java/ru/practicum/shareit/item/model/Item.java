package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
@Builder
public class Item {
    // уникальный идентификатор вещи
    private long id;
    // краткое название
    private String name;
    // развёрнутое описание
    private String description;
    // статус о том, доступна или нет вещь для аренды
    private Boolean available;
    // владелец вещи
    private long owner;
    // если вещь была создана по запросу другого пользователя, то в этом
    //поле будет храниться ссылка на соответствующий запрос
    private ItemRequest request;

}
