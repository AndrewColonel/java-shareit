package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemOwnerRequestDto;
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

    public static Item toItem(long userId, ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(userId);
        if (Objects.nonNull(itemDto.getRequest())) item.setRequest(itemDto.getRequest());
        return item;
    }

    public static Item toItem(Item oldItem, ItemPatchDto itemPatchDto) {
        // обновление полей может происходить в любой комбинации
        // название, описание и статус доступа к аренде.
        Item item = new Item();
        if (Objects.nonNull(itemPatchDto.getName())) item.setName(itemPatchDto.getName());
        if (Objects.nonNull(itemPatchDto.getDescription())) item.setDescription(itemPatchDto.getDescription());
        if (Objects.nonNull(itemPatchDto.getAvailable())) item.setAvailable(itemPatchDto.getAvailable());
        return item;
    }

    public static ItemOwnerRequestDto toItemRequestDto(Item item) {
        return ItemOwnerRequestDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .build();
    }

}