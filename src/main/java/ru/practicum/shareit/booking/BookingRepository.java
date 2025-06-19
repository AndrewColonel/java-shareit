package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;




public interface BookingRepository extends JpaRepository<Booking, Long> {

//    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, SpringDataWebProperties.Sort sort);

//    List<Booking> findByBooker(long bookerId);

}
