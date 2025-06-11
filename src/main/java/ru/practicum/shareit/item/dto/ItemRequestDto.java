package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequestDto {
    // модель для Просмотр владельцем списка всех
    // его вещей с указанием названия и описания для каждой из них
    // краткое название
    private String name;
    // развёрнутое описание
    private String description;
}
