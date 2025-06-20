package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemPatchDto itemPatchDto);

    ItemDto getItemById(long itemId);

    Collection<ItemRequestDto> findAllItems(long userId);

    Collection<ItemDto> searchItems(String searchQuery);
}
