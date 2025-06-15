package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Item save(long userId, Item item);

    Item update(long itemId, Item oldItem, Item newItem);

    Item findById(long itemId);

    Collection<Item> findAll(long userId);

    Collection<Item> search(String searchQuery);

}
