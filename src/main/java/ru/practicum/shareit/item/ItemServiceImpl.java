package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
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
        isUser(userId, userRepository.findAllUsers());
        return ItemMapper.toItemDto(
                itemRepository.save(userId,
                        ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemPatchDto itemPatchDto) {
        // валидация  itemId и userId выполняется контроллером
        // Редактировать вещь может только её владелец.
        // валидация владелца как зарегистрированного пользоыателя
        isUser(userId, userRepository.findAllUsers());
        // валидация пользователя как владельца
        isOwner(userId, itemId, itemRepository.findAllOwners());
        // Валидация вещи для обновления
        isItemPatchDto(itemPatchDto);
        return ItemMapper.toItemDto(
                itemRepository.update(itemId,
                        ItemMapper.toItem(itemPatchDto)));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        //доступнно всем
        // валидация  itemId и userId выполняется контроллером
        return ItemMapper.toItemDto(
                itemRepository.findById(itemId));
    }

    @Override
    public Collection<ItemRequestDto> findAllItems(long userId) {
        // доступно только зарегисритрованному владельцу
        // валидация  itemId и userId выполняется контроллером
        isUser(userId, userRepository.findAllUsers());
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
