package ru.practicum.shareit.gateway.item.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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
