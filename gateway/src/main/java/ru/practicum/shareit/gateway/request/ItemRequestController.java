package ru.practicum.shareit.gateway.request;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final RequestClient requestClient;



    //`POST /requests` — добавить новый запрос вещи.
    // Основная часть запроса — текст запроса, в котором пользователь описывает,
    // какая именно вещь ему нужна.
    // @PostMapping public ItemRequestDto create(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
    // @Valid @RequestBody NewItemRequestDto newItemRequestDto)

    //`GET /requests` — получить список своих запросов вместе с данными об ответах на них.
    // Для каждого запроса должны быть указаны описание, дата и время создания,
    // а также список ответов в формате: `id` вещи, название, `id` владельца.
    // В дальнейшем, используя указанные `id` вещей, можно будет получить подробную информацию о каждой из них.
    // Запросы должны возвращаться отсортированными от более новых к более старым.
    // GetMapping public List<ItemRequestAnswerDto> findAllByOwner(@Positive @RequestHeader("X-Sharer-User-Id") long userId)


    // `GET /requests/all` — получить список запросов, созданных другими пользователями.
    // С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
    // на которые они могли бы ответить. Запросы сортируются по дате создания от более новых к более старым.
    // GetMapping("/all") public List<ItemRequestDto> findAll(@Positive @RequestHeader("X-Sharer-User-Id") long userId)


    //`GET /requests/{requestId}` — получить данные об одном конкретном запросе вместе с данными об ответах на
    // него в том же формате, что и в эндпоинте `GET /requests`.
    // Посмотреть данные об отдельном запросе может любой пользователь.
    // GetMapping("/{requestId}") public ItemRequestAnswerDto getById(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
    // @Positive @PathVariable("requestId") long requestId)
}
