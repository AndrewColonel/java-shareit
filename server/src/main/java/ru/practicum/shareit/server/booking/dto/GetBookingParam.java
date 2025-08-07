package ru.practicum.shareit.server.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Data
@NoArgsConstructor
public class GetBookingParam {
    private String state;
    private Integer from;
    private Integer size;
    private Pageable pageable;

    public static GetBookingParam of(String state,
                                     Integer from,
                                     Integer size) {
        GetBookingParam getBookingParam = new GetBookingParam();
        getBookingParam.setState(state);
        getBookingParam.setFrom(from);
        getBookingParam.setSize(size);
        getBookingParam.setPageable(PageRequest.of(from > 0 ? from / size : 0, size));

        return getBookingParam;
    }
}
