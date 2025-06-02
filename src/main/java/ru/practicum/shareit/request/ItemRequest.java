package ru.practicum.shareit.request;
// класс, отвечающий за запрос вещи

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    // уникальный идентификатор запроса
    private long id;
    // текст запроса, содержащий описание требуемой вещи
    private String description;
    // пользователь, создавший запрос
    private long requestor;
    // дата и время создания запроса
    private LocalDateTime created;
}
