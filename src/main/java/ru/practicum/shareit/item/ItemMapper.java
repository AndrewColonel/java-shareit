package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Objects;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(Objects.nonNull(item.getRequest()) ? item.getRequest() : null)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .request(Objects.nonNull(itemDto.getRequest()) ? itemDto.getRequest() : null)
                .build();
    }

    public static Item toItem(ItemPatchDto itemPatchDto) {
        // обновление полей может происходить в любой комбинации
        // название, описание и статус доступа к аренде.
        return Item.builder()
                .name(Objects.nonNull(itemPatchDto.getName()) ?
                        itemPatchDto.getName() : null)
                .description(Objects.nonNull(itemPatchDto.getDescription()) ?
                        itemPatchDto.getDescription() : null)
                .available(Objects.nonNull(itemPatchDto.getAvailable()) ?
                        itemPatchDto.getAvailable() : null)
                .build();
    }

    public static ItemRequestDto toItemRequestDto(Item item) {
        return ItemRequestDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .build();
    }

}