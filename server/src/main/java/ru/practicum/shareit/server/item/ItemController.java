package ru.practicum.shareit.server.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.item.dto.*;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody NewItemDto newItemDto) {
        return itemService.createItem(userId, newItemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable("itemId") long itemId,
                          @RequestBody ItemPatchDto itemPatchDto) {
        return itemService.updateItem(userId, itemId, itemPatchDto);
    }

    @GetMapping("/{itemId}")
    public ItemViewingDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable("itemId") long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemOwnerViewingDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findAllItems(userId);
    }

    // /items/search?text={text}
    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam(value = "text", required = false) String searchQuery) {
        return itemService.searchItems(searchQuery);
    }

    // POST /items/{itemId}/comment
    @PostMapping("/{itemId}/comment")
    public CommentDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable("itemId") long itemId,
                             @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId,itemId, commentDto);
    }

}