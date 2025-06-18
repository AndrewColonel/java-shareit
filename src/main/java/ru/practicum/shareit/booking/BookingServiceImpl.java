package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.booking.BookingCheck.*;
import static ru.practicum.shareit.booking.BookingMapper.*;
import static ru.practicum.shareit.item.ItemCheck.*;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    // Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем,
    @Override
    public BookingDto createBooking(long userId, NewBookingDto newBookingDto) {
        isStartEndValid(newBookingDto); // валидация старта и конца брони
        // проверяем существоание такого пользователя
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        // проверяем существование вещи
        Item item = itemRepository.findById(newBookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", newBookingDto.getItemId())));
        isItemAvailable(item); // проверка доступности вещи для брони
        newBookingDto.setBooker(userId);
        newBookingDto.setStatus(Status.WAITING);
        return toBookingDto(bookingRepository.save(toBooking(newBookingDto)), user, item);
    }

    // Подтверждение или отклонение запроса на бронирование.
    @Override
    public BookingDto updateBooking(long userId, long bookingId, boolean approved) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ValidationException(String.format("Пользователь с ID %s не найден", userId)));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронирования  с ID %s не найден", bookingId)));
        Item item = itemRepository.findById(booking.getItem()).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", booking.getItem())));
        isOwner(userId, item.getOwner()); // Может быть выполнено только владельцем вещи.
        // необходимо получить пользователя запрошивающего бронь
        User booker = userRepository.findById(booking.getBooker()).orElseThrow(() ->
                new ValidationException(String.format("Пользователь с ID %s не найден", userId)));
        // Затем статус бронирования становится либо `APPROVED`,либо `REJECTED`
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return toBookingDto(bookingRepository.save(booking), booker, item);
    }

    // Получение данных о конкретном бронировании (включая его статус).
    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронирования  с ID %s не найден", bookingId)));
        Item item = itemRepository.findById(booking.getItem()).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", booking.getItem())));
        // Может быть выполнено либо автором бронирования, либо владельцем вещи,к которой относится бронирование.
        isBookerOrOwner(userId, booking.getBooker(), item.getOwner());
        // необходимо получить пользователя запрошивающего бронь
        User booker = userRepository.findById(booking.getBooker()).orElseThrow(() ->
                new ValidationException(String.format("Пользователь с ID %s не найден", userId)));
        return toBookingDto(booking, booker, item);
    }

    //- Получение списка всех бронирований текущего пользователя.
    // Эндпоинт — `GET /bookings?state={state}`.
    // Параметр `state` необязательный и по умолчанию равен **`ALL`** (англ. «все»).
    // Также он может принимать значения **`CURRENT`** (англ. «текущие»), **`PAST`** (англ. «завершённые»),
    // **`FUTURE`** (англ. «будущие»), **`WAITING`** (англ. «ожидающие подтверждения»),
    // **`REJECTED`** (англ. «отклонённые»).
    // Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
    @Override
    public Collection<BookingDto> findAllBookings(long userId, State state) {
        return List.of();
    }

    //- Получение списка бронирований для всех вещей текущего пользователя.
    // Эндпоинт — `GET /bookings/owner?state={state}`. Этот запрос имеет смысл для владельца хотя бы одной вещи.
    // Работа параметра `state` аналогична его работе в предыдущем сценарии.
    @Override
    public Collection<BookingDto> findAllOwnerBooking(long userId, State state) {
        return List.of();
    }

}
