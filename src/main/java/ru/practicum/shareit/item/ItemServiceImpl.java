package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemOwnerRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;

import static ru.practicum.shareit.item.ItemCheck.*;


@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        // валидация  userDto и userId выполняется контроллером
        //валидация владелца как зарегистрированного пользоыателя
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        // валидация пользователя как владельца
        isOwner(userId, itemDto.getOwner());
        return ItemMapper.toItemDto(
                itemRepository.save(userId,
                        ItemMapper.toItem(userId, itemDto)));
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemPatchDto itemPatchDto) {
        // валидация  itemId и userId выполняется контроллером
        // Редактировать вещь может только её владелец.
        // валидация объекта Вещь для обновления
        isItemPatchDto(itemPatchDto);
        // валидация владелца как зарегистрированного пользоыателя
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        // Валидация вещи для обновления
        Item oldItem = itemRepository.findById(itemId);
        // валидация пользователя как владельца
        isOwner(userId, oldItem.getOwner());
        return ItemMapper.toItemDto(
                itemRepository.update(itemId, oldItem,
                        ItemMapper.toItem(oldItem,itemPatchDto)));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        //доступнно всем
        // валидация  itemId и userId выполняется контроллером
        return ItemMapper.toItemDto(
                itemRepository.findById(itemId));
    }

    @Override
    public Collection<ItemOwnerRequestDto> findAllItems(long userId) {
        // доступно только зарегисритрованному владельцу
        // валидация  itemId и userId выполняется контроллером
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        return itemRepository.findAll(userId).stream()
                .map(ItemMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> searchItems(String searchQuery) {
        isStringQuery(searchQuery);
        return itemRepository.search(searchQuery).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
