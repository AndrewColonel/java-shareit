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
import java.util.Set;
import java.util.stream.Collectors;

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
        // валидация пользователя как владельца - isOwner(userId, itemDto.getOwner()) не проходитт по тесту)))
        return ItemMapper.toItemDto(
                itemRepository.save(ItemMapper.toItem(userId, itemDto)));
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
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", itemId)));
        // валидация пользователя как владельца
        isOwner(userId, oldItem.getOwner());
        return ItemMapper.toItemDto(
                itemRepository.save(ItemMapper.toItem(oldItem,itemPatchDto)));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        //доступнно всем
        // валидация  itemId и userId выполняется контроллером
        return ItemMapper.toItemDto(
                itemRepository.findById(itemId).orElseThrow(() ->
                        new NotFoundException(String.format("Вещь с ID %s не найдена", itemId))));
    }

    @Override
    public Collection<ItemOwnerRequestDto> findAllItems(long userId) {
        // доступно только зарегисритрованному владельцу
        // валидация  itemId и userId выполняется контроллером
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        return itemRepository.findByOwner(userId).stream()
                .map(ItemMapper::toItemOwnerRequestDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> searchItems(String searchQuery) {
        isStringQuery(searchQuery);
        if (searchQuery.isEmpty() || searchQuery.isBlank())
            return Set.of();
        return itemRepository.search(searchQuery).stream()
                .filter(item -> item.getAvailable().equals(true))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }
}
