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
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
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
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(long userId, NewItemDto newItemDto) {
        // валидация  userDto и userId выполняется контроллером
        //валидация владелца как зарегистрированного пользоыателя
        getUser(userId);
        // валидация пользователя как владельца - isOwner(userId, itemDto.getOwner()) не проходитт по тесту)))
        newItemDto.setOwner(userId);

        ItemRequest itemRequest = null;
        if (Objects.nonNull(newItemDto.getRequestId())) {
            itemRequest = getRequest(newItemDto.getRequestId());
            newItemDto.setRequestId(itemRequest.getId());
        }

        return toItemDto(
                itemRepository.save(toItem(newItemDto, itemRequest)));
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemPatchDto itemPatchDto) {
        // валидация  itemId и userId выполняется контроллером
        // Редактировать вещь может только её владелец.
        // валидация объекта Вещь для обновления
        isItemPatchDto(itemPatchDto);
        // валидация владелца как зарегистрированного пользоыателя
        getUser(userId);
        // Валидация вещи для обновления
        Item oldItem = getItem(itemId);
        // валидация пользователя как владельца
        isOwner(userId, oldItem.getOwner());
        return toItemDto(
                itemRepository.save(toItem(oldItem, itemPatchDto)));
    }

    @Override
    public ItemViewingDto getItemById(long userId, long itemId) {
        // валидация  itemId и userId
        getUser(userId);
        Item item = getItem(itemId);
        // нужно, чтобы владелец видел даты последнего и ближайшего следующего бронирования для каждой вещи
        // в соответсвии с тестами postman
        LocalDateTime lastBooking = null;
        LocalDateTime nextBooking = null;
        LocalDateTime requestTime = LocalDateTime.now();
        if (isOwnerBoolean(userId, item.getOwner())) {
            List<Booking> bookingListBefore =
                    bookingRepository.findByItem_IdAndEndIsBeforeOrderByEndAsc(itemId, requestTime);
            List<Booking> bookingListAfter =
                    bookingRepository.findByItem_IdAndStartIsAfterOrderByEndAsc(itemId, requestTime);
            if (!bookingListBefore.isEmpty()) lastBooking = bookingListBefore.getLast().getEnd();
            if (!bookingListAfter.isEmpty()) nextBooking = bookingListAfter.getFirst().getEnd();
        }
        List<Long> commentsList = commentRepository.findByItem_IdOrderByCreatedAsc(itemId)
                .stream()
                .map(Comment::getId)
                .toList();
        return toItemViewingDto(item, lastBooking, nextBooking, commentsList);
    }

    @Override
    public Collection<ItemOwnerViewingDto> findAllItems(long userId) {
        getUser(userId);
        // получаем списко вещей этого владельца
        List<Item> itemList = itemRepository.findByOwner(userId);
        // списко вещей владельца преобразуем в списко ID вещей
        List<Long> itemIdList = itemList.stream()
                .map(Item::getId)
                .toList();
        // получаю списко броней для данного списка ID вещей
        List<Booking> bookingList = bookingRepository.findByItem_IdInOrderByStartAsc(itemIdList);
        // собираю мапу для вещей
        Map<Long, List<Booking>> ownerBookingMap = bookingList.stream()
                .collect(groupingBy(booking -> booking.getItem().getId()));

        // получаю списко комментов для данного списка ID  вещей
        List<Comment> commentList = commentRepository.findByItem_IdInOrderByCreatedAsc(itemIdList);
        // собираю мапу для комментов
        Map<Long, List<Comment>> ownerCommentsMap = commentList.stream()
                .collect(groupingBy(comment -> comment.getItem().getId()));


        return itemList.stream()
                .map(item -> {
                    LocalDateTime lastBooking = null;
                    LocalDateTime nextBooking = null;
                    List<Long> comments = new ArrayList<>();
                    LocalDateTime requestTime = LocalDateTime.now();
                    if (Objects.nonNull(ownerBookingMap.get(item.getId()))) {
                        if (!ownerBookingMap.get(item.getId()).isEmpty()) {
                            List<LocalDateTime> lastBookingDateList = ownerBookingMap.get(item.getId()).stream()
                                    .map(Booking::getEnd)
                                    .filter(end -> end.isBefore(requestTime))
                                    .toList();
                            if (!lastBookingDateList.isEmpty())
                                lastBooking = lastBookingDateList.getLast();
                        }

                        if (!ownerBookingMap.get(item.getId()).isEmpty()) {
                            List<LocalDateTime> nextBookingDateList = ownerBookingMap.get(item.getId()).stream()
                                    .map(Booking::getEnd)
                                    .filter(end -> end.isAfter(requestTime))
                                    .toList();
                            if (!nextBookingDateList.isEmpty())
                                nextBooking = nextBookingDateList.getFirst();
                        }

                    }
                    if (Objects.nonNull(ownerCommentsMap.get(item.getId()))) {
                        if (!ownerCommentsMap.get(item.getId()).isEmpty())
                            comments.addAll(ownerCommentsMap.get(item.getId()).stream()
                                    .map(Comment::getId)
                                    .toList());
                    }

                    return ItemMapper.toItemOwnerRequestDtoV2(item, lastBooking, nextBooking, comments);
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
        User booker = getUser(userId);
        Item item = getItem(itemId);
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
                ? Optional.empty() : Optional.of(booking.getFirst().getEnd());
        return Map.of("last", lastBooking, "next", nextBooking);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", itemId)));
    }

    private ItemRequest getRequest(long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с ID %s не найден", requestId)));
    }

}
