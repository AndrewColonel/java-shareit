package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(long userId, NewItemRequestDto newItemRequestDto);

    List<ItemRequestAnswerDto> findAllByOwnerRequests(long userId);

    List<ItemRequestDto> findAllRequests(long userId);

    ItemRequestAnswerDto getRequestById(long userId, long requestId);
}
