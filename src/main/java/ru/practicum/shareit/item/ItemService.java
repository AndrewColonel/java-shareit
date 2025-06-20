package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(long userId, NewItemDto newItemDto);

    ItemDto updateItem(long userId, long itemId, ItemPatchDto itemPatchDto);

    ItemViewingDto getItemById(long itemId);

    Collection<ItemOwnerViewingDto> findAllItems(long userId);

    Collection<ItemDto> searchItems(String searchQuery);

}