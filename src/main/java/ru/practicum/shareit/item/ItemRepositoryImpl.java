package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

import static java.util.stream.Collectors.*;

@Repository
@AllArgsConstructor
public class ItemRepositoryImpl
//        implements ItemRepository
{

    // хранилище вещей
    private final Map<Long, Item> items = new HashMap<>();

//    @Override
    public Item save(long userId, Item item) {
        item.setId(getNextId());
        item.setOwner(userId);
        items.put(item.getId(), item);
        return item;
    }

//    @Override
    public Item update(long itemId, Item oldItem, Item newItem) {
        if (Objects.nonNull(newItem.getName())) oldItem.setName(newItem.getName());
        if (Objects.nonNull(newItem.getDescription())) oldItem.setDescription(newItem.getDescription());
        if (Objects.nonNull(newItem.getAvailable())) oldItem.setAvailable(newItem.getAvailable());
        return oldItem;
    }

//    @Override
    public Item findById(long itemId) {
        // Информацию о конкретной вещи по её идентификатору
        // может просмотреть любой пользователь.
        Item item = items.get(itemId);
        if (Objects.isNull(item)) throw new NotFoundException(
                String.format("Вещь с ID %s не найдена", itemId));
        return item;
    }

//    @Override
    public List<Item> findAll(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .toList();
    }

//    @Override
    public Set<Item> search(String searchQuery) {
        if (searchQuery.isEmpty() || searchQuery.isBlank())
            return Set.of();
        return items.values().stream()
                .filter(item ->
                        (item.getAvailable().equals(true)
                                && (item.getName().toLowerCase().contains(searchQuery.toLowerCase())
                                || item.getDescription().toLowerCase().contains(searchQuery.toLowerCase()))))
                .collect(toSet());
    }

    // вспомогательный метод получения следующего значения id
    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
