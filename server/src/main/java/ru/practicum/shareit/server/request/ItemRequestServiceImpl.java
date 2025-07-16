package ru.practicum.shareit.server.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.ItemMapper;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.NewItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemAnswerDto;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.*;
import static ru.practicum.shareit.server.request.ItemRequestMapper.*;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(long userId, NewItemRequestDto newItemRequestDto) {
        getUser(userId);
        return toItemRequestDto(itemRequestRepository.save(toItemRequest(userId, newItemRequestDto)));
    }

    @Override
    public List<ItemRequestAnswerDto> findAllByOwnerRequests(long userId) {
        getUser(userId);
        // находим список запросов пользователя
        List<ItemRequest> itemRequestList = itemRequestRepository.findByRequestorOrderByCreatedAsc(userId);
        // получаем список ID запросов этго пользователя для запроса соответсвующих ответов - списка вещей
        List<Long> itemRequestIdList = itemRequestList.stream()
                .map(ItemRequest::getId)
                .toList();
        // получаем соотвесвующий ответ по запрошенным вещам
        List<Item> itemAnswerInterfaceList = itemRepository.findByRequest_idIn(itemRequestIdList);
        // получаю мапу из ID запросов и списков ответов
        Map<Long, List<ItemAnswerDto>> itemAnswerMap = itemAnswerInterfaceList.stream()
                .collect(groupingBy(item -> item.getRequest().getId(),
                        mapping(ItemMapper::toItemAnswer, toList())));
        return itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestAnswerDto)
                .peek(itemRequestAnswerDto -> {
                    if (Objects.nonNull(itemAnswerMap.get(itemRequestAnswerDto.getId()))) {
                        if (!itemAnswerMap.get(itemRequestAnswerDto.getId()).isEmpty())
                            itemRequestAnswerDto.setItems(itemAnswerMap.get(itemRequestAnswerDto.getId()));
                    }
                })
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
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос с ID %s не найден", requestId)));
        // получаю списко ответов на данный запрос
        List<ItemAnswerDto> itemAnswerDtoList = itemRepository.findByRequest_id(requestId).stream()
                .map(ItemMapper::toItemAnswer)
                .toList();
        // если есть ответы, добавляю их список в поле запроса
        if (!itemAnswerDtoList.isEmpty()) itemRequest.setItems(itemAnswerDtoList);
        return toItemRequestAnswerDto(itemRequest);
    }

    // вспомогательные методы
    private void getUser(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
    }

}
