package ru.practicum.shareit.gateway.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.request.dto.NewItemRequestDto;


@Controller
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final RequestClient requestClient;


    //`POST /requests` — добавить новый запрос вещи.
    // Основная часть запроса — текст запроса, в котором пользователь описывает,
    // какая именно вещь ему нужна.
    @PostMapping
    public ResponseEntity<Object> create(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody NewItemRequestDto newItemRequestDto) {
        log.info("Запрос пользователя {} на создание запроса {}", userId,newItemRequestDto);
        return requestClient.createRequest(userId, newItemRequestDto);
    }

    //`GET /requests` — получить список своих запросов вместе с данными об ответах на них.
    // Для каждого запроса должны быть указаны описание, дата и время создания,
    // а также список ответов в формате: `id` вещи, название, `id` владельца.
    // В дальнейшем, используя указанные `id` вещей, можно будет получить подробную информацию о каждой из них.
    // Запросы должны возвращаться отсортированными от более новых к более старым.
    @GetMapping
    public ResponseEntity<Object> findAllByOwner(@Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на список запросов текущщего пользователя {}", userId);
        return requestClient.fiindAllRequestsByOwner(userId);
    }


    // `GET /requests/all` — получить список запросов, созданных другими пользователями.
    // С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
    // на которые они могли бы ответить. Запросы сортируются по дате создания от более новых к более старым.
    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос пользователя {} на список запросов, созданных другими пользователями",userId);
        return requestClient.findAllRequests(userId);
    }


    //`GET /requests/{requestId}` — получить данные об одном конкретном запросе вместе с данными об ответах на
    // него в том же формате, что и в эндпоинте `GET /requests`.
    // Посмотреть данные об отдельном запросе может любой пользователь.
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                          @Positive @PathVariable("requestId") long requestId) {
        log.info("Запрос на получение данных об запросе {} вместе с данными об ответах", requestId);
        return requestClient.getRequestById(userId, requestId);
    }
}
