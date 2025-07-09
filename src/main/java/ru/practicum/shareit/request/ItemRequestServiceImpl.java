package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static ru.practicum.shareit.request.ItemRequestMapper.*;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createItemRequest(long userId, NewItemRequestDto newItemRequestDto) {
        getUser(userId);
        return toItemRequestDto(itemRequestRepository.save(toItemRequest(userId, newItemRequestDto)));
    }

    @Override
    public List<ItemRequestAnswerDto> findAllByOwnerRequests(long userId) {
        getUser(userId);
        return itemRequestRepository.findByRequestorOrderByCreatedAsc(userId).stream()
                .map(ItemRequestMapper::toItemRequestAnswerDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> findAllRequests(long userId) {
        getUser(userId);
        return itemRequestRepository.findByRequestorNotOrderByCreatedAsc(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestAnswerDto getRequestById(long userId, long requestId) {
        getUser(userId);
        return toItemRequestAnswerDto(itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос с ID %s не найден", requestId))));
    }

    // вспомогательные методы
    private void getUser(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
    }

}
