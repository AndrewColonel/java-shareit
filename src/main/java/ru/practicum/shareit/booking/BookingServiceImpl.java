package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static ru.practicum.shareit.booking.BookingCheck.*;
import static ru.practicum.shareit.booking.BookingMapper.*;
import static ru.practicum.shareit.item.ItemCheck.*;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    //Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем,
    // а затем подтверждён владельцем вещи.
    // Эндпоинт — `POST /bookings` После создания запрос находится в статусе `WAITING` — «ожидает подтверждения».
    @Override
    public BookingDto createBooking(long userId, NewBookingDto newBookingDto ) {
        isStartEndValid(newBookingDto);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        Item item = itemRepository.findById(newBookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", newBookingDto.getItemId())));
        isItemAvailable(item);
        newBookingDto.setBooker(userId);
        newBookingDto.setStatus(Status.WAITING);
        return toBookingDto(bookingRepository.save(toBooking(newBookingDto)),user, item);
    }

    //- Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
    // Затем статус бронирования становится либо `APPROVED`,либо `REJECTED`.
    // Эндпоинт — `PATCH /bookings/{bookingId}?approved={approved}`,
    // параметр `approved` может принимать значения `true` или `false`.
    @Override
    public BookingDto updateBooking(long userId, long bookingId, boolean approved) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронирования  с ID %s не найден", bookingId)));
        Item item = itemRepository.findById(booking.getItem()).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", booking.getItem())));
        isOwner(userId, item.getOwner());
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return toBookingDto(bookingRepository.save(booking),user,item);
    }

    //- Получение данных о конкретном бронировании (включая его статус).
    // Может быть выполнено либо автором бронирования, либо владельцем вещи,
    // к которой относится бронирование.
    // Эндпоинт — `GET /bookings/{bookingId}`.
    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронирования  с ID %s не найден", bookingId)));
        Item item = itemRepository.findById(booking.getItem()).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", booking.getItem())));
        isOwner(userId, item.getOwner());
        isBooker(userId, booking.getBooker());
        return toBookingDto(booking,user,item);
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
        return null;
    }

    //- Получение списка бронирований для всех вещей текущего пользователя.
    // Эндпоинт — `GET /bookings/owner?state={state}`. Этот запрос имеет смысл для владельца хотя бы одной вещи.
    // Работа параметра `state` аналогична его работе в предыдущем сценарии.
    @Override
    public Collection<BookingDto> findAllOwnerBooking(long userId, State state) {
        return null;
    }

}
