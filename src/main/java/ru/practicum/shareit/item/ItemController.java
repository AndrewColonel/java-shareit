package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemOwnerRequestDto;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                          @Positive @PathVariable("itemId") long itemId,
                          @Valid @RequestBody ItemPatchDto itemPatchDto) {
        return itemService.updateItem(userId, itemId, itemPatchDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@Positive @PathVariable("itemId") long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemOwnerRequestDto> findAll(@Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findAllItems(userId);
    }

    // /items/search?text={text}
    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam(value = "text", required = false) String searchQuery) {
        return itemService.searchItems(searchQuery);
    }

}
