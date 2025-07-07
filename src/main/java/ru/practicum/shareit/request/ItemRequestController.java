package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

//`POST /requests` — добавить новый запрос вещи.
// Основная часть запроса — текст запроса, в котором пользователь описывает, какая именно вещь ему нужна.
@PostMapping


//`GET /requests` — получить список своих запросов вместе с данными об ответах на них.
// Для каждого запроса должны быть указаны описание, дата и время создания,
// а также список ответов в формате: `id` вещи, название, `id` владельца.
// В дальнейшем, используя указанные `id` вещей, можно будет получить подробную информацию о каждой из них.
// Запросы должны возвращаться отсортированными от более новых к более старым.
@GetMapping


// `GET /requests/all` — получить список запросов, созданных другими пользователями.
// С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
// на которые они могли бы ответить. Запросы сортируются по дате создания от более новых к более старым.
@GetMapping


//`GET /requests/{requestId}` — получить данные об одном конкретном запросе вместе с данными об ответах на
// него в том же формате, что и в эндпоинте `GET /requests`.
// Посмотреть данные об отдельном запросе может любой пользователь.
@GetMapping


}
