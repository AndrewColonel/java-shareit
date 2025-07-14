package ru.practicum.shareit.gateway.item;


import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.client.BaseClient;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemPatchDto;
import ru.practicum.shareit.gateway.item.dto.NewItemDto;

import java.util.Objects;
import java.util.Set;

import static ru.practicum.shareit.gateway.common.GatewayCheckUtility.isStringQuery;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long userId, NewItemDto newItemDto) {
        return post("", userId, newItemDto);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemPatchDto itemPatchDto) {
        return patch("/" + itemId, userId, itemPatchDto);
    }

    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        return get("/" + userId, itemId);
    }

    public ResponseEntity<Object> findAllItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItems(String searchQuery) {
        isStringQuery(searchQuery);
        return get("/search?text=" + searchQuery);
    }

    public ResponseEntity<Object> sreateComments(long userId, long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId,commentDto);
    }
}
