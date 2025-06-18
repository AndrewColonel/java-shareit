package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemOwnerRequestDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Objects;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static Item toItem(NewItemDto newItemDto) {
        Item item = new Item();
        item.setName(newItemDto.getName());
        item.setDescription(newItemDto.getDescription());
        item.setAvailable(newItemDto.getAvailable());
        item.setOwner(newItemDto.getOwner());
        if (Objects.nonNull(newItemDto.getRequest())) item.setRequest(newItemDto.getRequest());
        return item;
    }

    public static Item toItem(Item oldItem, ItemPatchDto itemPatchDto) {
        // обновление полей может происходить в любой комбинации
        // название, описание и статус доступа к аренде.
        if (Objects.nonNull(itemPatchDto.getName())) oldItem.setName(itemPatchDto.getName());
        if (Objects.nonNull(itemPatchDto.getDescription())) oldItem.setDescription(itemPatchDto.getDescription());
        if (Objects.nonNull(itemPatchDto.getAvailable())) oldItem.setAvailable(itemPatchDto.getAvailable());
        return oldItem;
    }

    public static ItemOwnerRequestDto toItemOwnerRequestDto(Item item) {
        return ItemOwnerRequestDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .build();
    }

}