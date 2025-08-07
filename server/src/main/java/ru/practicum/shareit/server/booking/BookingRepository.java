package ru.practicum.shareit.server.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.booking.model.Booking;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // запросные методы для получение данных о бронировании текущего пользователя, включая его статус
    List<Booking> findByBooker_IdOrderByStartAsc(long bookerId, Pageable pageable);

    List<Booking> findByBooker_IdAndStatusIsOrderByStartAsc(long bookerId, Status status);


    // CURRENT
    List<Booking> findByBooker_IdAndStatusIsAndEndIsAfterOrderByStartAsc(long bookerId, Status status,
                                                                         LocalDateTime requestDateTime);

    // PAST
    List<Booking> findByBooker_IdAndStatusIsAndEndIsBeforeOrderByStartAsc(long bookerId, Status status,
                                                                          LocalDateTime requestDateTime);

    // FUTURE
    List<Booking> findByBooker_IdAndStatusIsAndStartIsAfterOrderByStartAsc(long bookerId, Status status,
                                                                           LocalDateTime requestDateTime);


    // запросные методы для получение данных о бронировании для всех вещей текущего пользователя,включая его статус
    // здесь нужен запрос не ByBooker_Id, а ByItem_Owner_Id
    List<Booking> findByItemOwnerOrderByStartAsc(long bookerId);

    List<Booking> findByItemOwnerAndStatusIsOrderByStartAsc(long bookerId, Status status);

    // CURRENT
    List<Booking> findByItemOwnerAndStatusIsAndEndIsAfterOrderByStartAsc(long bookerId, Status status,
                                                                         LocalDateTime requestDateTime);

    // PAST
    List<Booking> findByItemOwnerAndStatusIsAndEndIsBeforeOrderByStartAsc(long bookerId, Status status,
                                                                          LocalDateTime requestDateTime);

    // FUTURE
    List<Booking> findByItemOwnerAndStatusIsAndStartIsAfterOrderByStartAsc(long bookerId, Status status,
                                                                           LocalDateTime requestDateTime);

    // запросные методы для ItemService
    List<Booking> findByItem_IdAndEndIsBeforeOrderByEndAsc(Long itemId, LocalDateTime end);

    List<Booking> findByItem_IdAndStartIsAfterOrderByEndAsc(Long itemId, LocalDateTime start);

    Optional<Booking> findByBooker_IdAndItem_IdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime end);

    List<Booking> findByItem_IdInOrderByStartAsc(List<Long> itemList);
}
