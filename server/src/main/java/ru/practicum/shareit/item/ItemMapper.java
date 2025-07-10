package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemAnswerDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .requestId(Objects.nonNull(item.getRequest()) ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(NewItemDto newItemDto, ItemRequest itemRequest) {
        Item item = new Item();
        item.setName(newItemDto.getName());
        item.setDescription(newItemDto.getDescription());
        item.setAvailable(newItemDto.getAvailable());
        item.setOwner(newItemDto.getOwner());
        if (Objects.nonNull(itemRequest)) item.setRequest(itemRequest);

        return item;
    }

    public static Item toItem(Item oldItem, ItemPatchDto itemPatchDto) {
        // обновление полей может происходить в любой комбинации
        // название, описание и статус доступа к аренде.
        if (Objects.nonNull(itemPatchDto.getName())) oldItem.setName(itemPatchDto.getName());
        if (Objects.nonNull(itemPatchDto.getDescription())) oldItem.setDescription(itemPatchDto.getDescription());
        if (Objects.nonNull(itemPatchDto.getAvailable())) oldItem.setAvailable(itemPatchDto.getAvailable());
        return oldItem;
    }

    public static ItemOwnerViewingDto toItemOwnerRequestDto(Item item) {
        return ItemOwnerViewingDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .build();
    }

    public static ItemOwnerViewingDto toItemOwnerRequestDtoV2(
            Item item, LocalDateTime lastBooking, LocalDateTime nextBooking, List<Long> commentsId) {
        return ItemOwnerViewingDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentsId)
                .build();
    }

    public static ItemViewingDto toItemViewingDto(
            Item item, LocalDateTime lastBooking, LocalDateTime nextBooking, List<Long> commentsList) {
        return ItemViewingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentsList)
                .build();
    }

    public static ItemAnswerDto toItemAnswer(Item item) {
        return ItemAnswerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .owner(item.getOwner())
                .build();
    }

}