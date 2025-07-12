package ru.practicum.shareit.server.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemPatchDto {
    // модель для обновления
    // Изменить можно название, описание и статус доступа к аренде.
    // Редактировать вещь может только её владелец.

     // краткое название
    private String name;
    // развёрнутое описание
    private String description;
    // статус о том, доступна или нет вещь для аренды
    private Boolean available;

}
