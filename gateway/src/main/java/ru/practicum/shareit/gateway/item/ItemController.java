package ru.practicum.shareit.gateway.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemClient itemClient;



    // PostMapping  public ItemDto create(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
    // @Valid @RequestBody NewItemDto newItemDto)

    // PatchMapping("/{itemId}") public ItemDto update(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
    // @Positive @PathVariable("itemId") long itemId, @Valid @RequestBody ItemPatchDto itemPatchDto)

    // GetMapping("/{itemId}") public ItemViewingDto getById(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
    // @Positive @PathVariable("itemId") long itemId)

    // @GetMapping public Collection<ItemOwnerViewingDto> findAll(
    // @Positive @RequestHeader("X-Sharer-User-Id") long userId)

    // /items/search?text={text}
    // GetMapping("/search") public Collection<ItemDto> search(@RequestParam(value = "text", required = false) String searchQuery)

    // POST /items/{itemId}/comment
    // PostMapping("/{itemId}/comment") public CommentDto create(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
    // @Positive @PathVariable("itemId") long itemId, @Valid @RequestBody CommentDto commentDto)

}