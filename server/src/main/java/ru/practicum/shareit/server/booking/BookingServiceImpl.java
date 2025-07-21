package ru.practicum.shareit.server.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingStateRequestDto;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static ru.practicum.shareit.server.common.ServerCheckUtility.*;
import static ru.practicum.shareit.server.booking.BookingMapper.*;


@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    // Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем,
    @Override
    public BookingDto createBooking(long userId, NewBookingDto newBookingDto) {
        // проверяем существоание такого пользователя
        User booker = getUser(userId);
        // проверяем существование вещи
        Item item = getItem(newBookingDto.getItemId());
        // проверка доступности вещи для брони
        isItemAvailable(item);
        return toBookingDto(bookingRepository.save(toBooking(newBookingDto, booker, item)));
    }

    // Подтверждение или отклонение запроса на бронирование.
    @Override
    public BookingDto updateBooking(long userId, long bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus().equals(Status.WAITING)) {
            // Может быть выполнено только владельцем вещи.
            isOwner(userId, booking.getItem().getOwner());
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        } else throw new ValidationException(String.format("Бронирование с ID %S уже подтверждено", bookingId));
        return toBookingDto(bookingRepository.save(booking));
    }

    // Получение данных о конкретном бронировании (включая его статус).
    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        Booking booking = getBooking(bookingId);
        // Может быть выполнено либо автором бронирования, либо владельцем вещи,к которой относится бронирование.
        isBookerOrOwner(userId, booking.getBooker().getId(), booking.getItem().getOwner());
        return toBookingDto(booking);
    }

    // Получение списка всех бронирований текущего пользователя.
    // Эндпоинт — `GET /bookings?state={state}`.
    // Параметр `state` необязательный и по умолчанию равен ALL  -все
    // Также он может принимать значения^
    // CURRENT - «текущие»
    // PAST - «завершённые»
    // FUTURE - «будущие»
    // WAITING - «ожидающие подтверждения»
    // REJECTED - «отклонённые»
    // Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
    @Override
    public Collection<BookingStateRequestDto> findAllBookings(long userId, String requestState) {
        getUser(userId);
        State state = stateConvert(requestState);
        List<BookingStateRequestDto> bookingStateRequestDtoList = new ArrayList<>();
        LocalDateTime requestDateTime = LocalDateTime.now();
        switch (state) {
            case ALL -> {
                bookingStateRequestDtoList = bookingRepository.findByBooker_IdOrderByStartAsc(userId)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }

            case WAITING -> {
                bookingStateRequestDtoList = bookingRepository.findByBooker_IdAndStatusIsOrderByStartAsc(userId,
                                Status.WAITING)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }
            case REJECTED -> {
                bookingStateRequestDtoList = Stream.concat(
                        bookingRepository.findByBooker_IdAndStatusIsOrderByStartAsc(
                                        userId, Status.REJECTED)
                                .stream()
                                .map(booking -> toBookingStateRequestDto(booking, requestDateTime)),
                        bookingRepository.findByBooker_IdAndStatusIsOrderByStartAsc(
                                        userId, Status.CANCELED)
                                .stream()
                                .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                ).toList();
            }
            case CURRENT -> {
                bookingStateRequestDtoList = bookingRepository
                        .findByBooker_IdAndStatusIsAndEndIsAfterOrderByStartAsc(
                                userId, Status.APPROVED, requestDateTime)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .filter(bookingStateRequestDto -> bookingStateRequestDto.getStatus().equals(State.CURRENT))
                        .toList();
            }
            case PAST -> {
                bookingStateRequestDtoList = bookingRepository
                        .findByBooker_IdAndStatusIsAndEndIsBeforeOrderByStartAsc(
                                userId, Status.APPROVED, requestDateTime)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }
            case FUTURE -> {
                bookingStateRequestDtoList = bookingRepository
                        .findByBooker_IdAndStatusIsAndStartIsAfterOrderByStartAsc(
                                userId, Status.APPROVED, requestDateTime)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }
        }
        return bookingStateRequestDtoList;
    }

    // Получение списка бронирований для всех вещей текущего пользователя.
    // Эндпоинт — `GET /bookings/owner?state={state}`. Этот запрос имеет смысл для владельца хотя бы одной вещи.
    // Работа параметра `state` аналогична его работе в предыдущем сценарии.
    @Override
    public Collection<BookingStateRequestDto> findAllOwnerBooking(long userId, String requestState) {
        getUser(userId);
        State state = stateConvert(requestState);
        LocalDateTime requestDateTime = LocalDateTime.now();
        List<BookingStateRequestDto> bookingStateRequestDtoList = new ArrayList<>();
        switch (state) {
            case ALL -> {
                bookingStateRequestDtoList = bookingRepository.findByItemOwnerOrderByStartAsc(userId)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }

            case WAITING -> {
                bookingStateRequestDtoList = bookingRepository.findByItemOwnerAndStatusIsOrderByStartAsc(userId,
                                Status.WAITING)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }
            case REJECTED -> {
                bookingStateRequestDtoList = Stream.concat(
                        bookingRepository.findByItemOwnerAndStatusIsOrderByStartAsc(
                                        userId, Status.REJECTED)
                                .stream()
                                .map(booking -> toBookingStateRequestDto(booking, requestDateTime)),
                        bookingRepository.findByItemOwnerAndStatusIsOrderByStartAsc(
                                        userId, Status.CANCELED)
                                .stream()
                                .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                ).toList();

            }
            case CURRENT -> {
                bookingStateRequestDtoList = bookingRepository
                        .findByItemOwnerAndStatusIsAndEndIsAfterOrderByStartAsc(
                                userId, Status.APPROVED, requestDateTime)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .filter(bookingStateRequestDto -> bookingStateRequestDto.getStatus().equals(State.CURRENT))
                        .toList();
            }
            case PAST -> {
                bookingStateRequestDtoList = bookingRepository
                        .findByItemOwnerAndStatusIsAndEndIsBeforeOrderByStartAsc(
                                userId, Status.APPROVED, requestDateTime)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }
            case FUTURE -> {
                bookingStateRequestDtoList = bookingRepository
                        .findByItemOwnerAndStatusIsAndStartIsAfterOrderByStartAsc(
                                userId, Status.APPROVED, requestDateTime)
                        .stream()
                        .map(booking -> toBookingStateRequestDto(booking, requestDateTime))
                        .toList();
            }

        }
        return bookingStateRequestDtoList;
    }


    // вспомогательные методы
    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", itemId)));
    }

    private Booking getBooking(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронирования  с ID %s не найден", bookingId)));
    }

    private State stateConvert(String requestState) {
        State state;
        try {
            state = State.valueOf(requestState.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Параметр запроса статуса state=%s, не верен", requestState));
        }
        return state;
    }
}
