package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemOwnerRequestDto;
import ru.practicum.shareit.item.dto.NewItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(long userId, NewItemDto newItemDto);

    ItemDto updateItem(long userId, long itemId, ItemPatchDto itemPatchDto);

    ItemDto getItemById(long itemId);

    Collection<ItemOwnerRequestDto> findAllItems(long userId);

    Collection<ItemDto> searchItems(String searchQuery);
}
