package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemOwnerViewingDto {
    // модель для Просмотр владельцем списка всех
    // его вещей с указанием названия и описания для каждой из них
    // краткое название
    private String name;
    // развёрнутое описание
    private String description;
    private ItemRequest request;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private String comments;

}