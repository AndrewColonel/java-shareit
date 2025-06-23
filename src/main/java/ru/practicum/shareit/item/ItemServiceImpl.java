package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.common.CheckUtility.*;
import static ru.practicum.shareit.item.CommentMapper.*;
import static ru.practicum.shareit.item.ItemMapper.*;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(long userId, NewItemDto newItemDto) {
        // валидация  userDto и userId выполняется контроллером
        //валидация владелца как зарегистрированного пользоыателя
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        // валидация пользователя как владельца - isOwner(userId, itemDto.getOwner()) не проходитт по тесту)))
        newItemDto.setOwner(userId);
        return toItemDto(
                itemRepository.save(toItem(newItemDto)));
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
        return toItemDto(
                itemRepository.save(toItem(oldItem, itemPatchDto)));
    }

    @Override
    public ItemViewingDto getItemById(long userId, long itemId) {
        // валидация  itemId и userId
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", itemId)));
        // нужно, чтобы владелец видел даты последнего и ближайшего следующего бронирования для каждой вещи
        // в соответсвии с тестами postman
        LocalDateTime lastBooking = null;
        LocalDateTime nextBooking = null;
        if (isOwnerBoolean(userId, item.getOwner())) {
            if (getBookingDateTime(itemId).get("last").isPresent())
                lastBooking = getBookingDateTime(itemId).get("last").get();
            if (getBookingDateTime(itemId).get("next").isPresent())
                nextBooking = getBookingDateTime(itemId).get("next").get();
        }
        List<Long> commentsList = commentRepository.findByAuthor_IdAndItem_IdOrderByCreatedAsc(userId, itemId)
                .stream()
                .map(Comment::getId)
                .toList();
        return toItemViewingDto(item, lastBooking, nextBooking, commentsList);
    }

    @Override
    public Collection<ItemOwnerViewingDto> findAllItems(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        return itemRepository.findByOwner(userId).stream()
                .map(item -> {
                    // нужно, чтобы владелец видел даты последнего и ближайшего следующего бронирования
                    // для каждой вещи когда просматривает список (`GET /items`).
                    LocalDateTime lastBooking = null;
                    LocalDateTime nextBooking = null;
                    if (getBookingDateTime(item.getId()).get("last").isPresent())
                        lastBooking = getBookingDateTime(item.getId()).get("last").get();
                    if (getBookingDateTime(item.getId()).get("next").isPresent())
                        nextBooking = getBookingDateTime(item.getId()).get("next").get();
                    return ItemMapper.toItemOwnerRequestDtoV2(item, lastBooking, nextBooking);
                })
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

    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        User booker = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", itemId)));
        //Отзыв может оставить только тот пользователь, который брал эту вещь в аренду, и только после
        //окончания срока аренды.
        LocalDateTime requestTime = LocalDateTime.now();
        bookingRepository.findByBooker_IdAndItem_IdAndEndIsBefore(userId, itemId, requestTime)
                .orElseThrow(() ->
                        new ValidationException(String.format("Пользователь с ID %s не пользовался вещью с ID %s",
                                userId, itemId)));
        return toCommentDto(commentRepository.save(toComment(commentDto, booker, item)));
    }

    // вспомогательный метод получения дат заказов
    public Map<String, Optional<LocalDateTime>> getBookingDateTime(long itemId) {
        LocalDateTime requestTime = LocalDateTime.now();
        List<Booking> booking =
                bookingRepository.findByItem_IdAndEndIsBeforeOrderByEndAsc(itemId, requestTime);
        Optional<LocalDateTime> lastBooking = booking.isEmpty()
                ? Optional.empty() : Optional.of(booking.getLast().getEnd());
        booking = bookingRepository.findByItem_IdAndStartIsAfterOrderByEndAsc(itemId, requestTime);
        Optional<LocalDateTime> nextBooking = booking.isEmpty()
                ? Optional.empty() : Optional.of(booking.getLast().getEnd());
        return Map.of("last", lastBooking, "next", nextBooking);
    }
}
