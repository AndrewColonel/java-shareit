package ru.practicum.shareit.gateway.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemPatchDto;
import ru.practicum.shareit.gateway.item.dto.NewItemDto;


@Controller
@RequestMapping(path = "/items")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> create(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody NewItemDto newItemDto) {
        return itemClient.createItem(userId, newItemDto);
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                         @Positive @PathVariable("itemId") long itemId,
                                         @Valid @RequestBody ItemPatchDto itemPatchDto) {
        return itemClient.updateItem(userId, itemId, itemPatchDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                          @Positive @PathVariable("itemId") long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(
            @Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.findAllItems(userId);
    }

    // /items/search?text={text}
    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(value = "text", required = false) String searchQuery) {
        return itemClient.searchItems(searchQuery);
    }

    // POST /items/{itemId}/comment
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> create(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                         @Positive @PathVariable("itemId") long itemId,
                                         @Valid @RequestBody CommentDto commentDto) {
        return itemClient.sreateComments(userId, itemId, commentDto);
    }

}