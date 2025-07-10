package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewItemDto {
    // уникальный идентификатор вещи
    private long id;
    // краткое название
    @NotBlank
    private String name;
    // развёрнутое описание
    @NotBlank
    private String description;
    // статус о том, доступна или нет вещь для аренды
    @NotNull
    private Boolean available;
    // владелец вещи
    private long owner;
    // если вещь была создана по запросу другого пользователя, то в этом
    //поле будет храниться ссылка на соответствующий запрос
    @Positive
    private Long requestId;
}
