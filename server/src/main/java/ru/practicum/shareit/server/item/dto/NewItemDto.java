package ru.practicum.shareit.server.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewItemDto {
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
    private Long requestId;
}
