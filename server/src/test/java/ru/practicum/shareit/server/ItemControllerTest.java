package ru.practicum.shareit.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.dto.*;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final long USER_ID = 1L;
    private static final long ITEM_ID = 100L;
    private static final long COMMENT_ID = 200L;

    // --- POST /items ---

    @Test
    void testCreateItem_success() throws Exception {
        NewItemDto newItemDto = new NewItemDto();
        newItemDto.setName("Drill");
        newItemDto.setDescription("Powerful drill");
        newItemDto.setAvailable(true);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(ITEM_ID);
        itemDto.setName("Drill");
        itemDto.setAvailable(true);

        when(itemService.createItem(USER_ID, newItemDto)).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ITEM_ID))
                .andExpect(jsonPath("$.name").value("Drill"));

        verify(itemService, times(1)).createItem(USER_ID, newItemDto);
    }

    @Test
    void testCreateItem_userNotFound_throwsNotFoundException() throws Exception {
        NewItemDto newItemDto = new NewItemDto();
        newItemDto.setName("Drill");

        when(itemService.createItem(USER_ID, newItemDto))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользователь не найден"));

        verify(itemService, times(1)).createItem(USER_ID, newItemDto);
    }

    // --- PATCH /items/{itemId} ---

    @Test
    void testUpdateItem_success() throws Exception {
        ItemPatchDto patchDto = new ItemPatchDto();
        patchDto.setName("Updated Drill");

        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(ITEM_ID);
        updatedItem.setName("Updated Drill");
        updatedItem.setAvailable(true);

        when(itemService.updateItem(USER_ID, ITEM_ID, patchDto)).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{itemId}", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Drill"));

        verify(itemService, times(1)).updateItem(USER_ID, ITEM_ID, patchDto);
    }

    @Test
    void testUpdateItem_notOwner_throwsNotFoundException() throws Exception {
        ItemPatchDto patchDto = new ItemPatchDto();
        patchDto.setName("Updated Drill");

        when(itemService.updateItem(USER_ID, ITEM_ID, patchDto))
                .thenThrow(new NotFoundException("Пользователь не владелец"));

        mockMvc.perform(patch("/items/{itemId}", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользователь не владелец"));

        verify(itemService, times(1)).updateItem(USER_ID, ITEM_ID, patchDto);
    }

    // --- GET /items/{itemId} ---

    @Test
    void testGetItemById_success() throws Exception {
        ItemViewingDto itemDto = new ItemViewingDto();
        itemDto.setId(ITEM_ID);
        itemDto.setName("Drill");
        itemDto.setComments(List.of(1L));

        when(itemService.getItemById(USER_ID, ITEM_ID)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ITEM_ID))
                .andExpect(jsonPath("$.comments[0]").value(1L));

        verify(itemService, times(1)).getItemById(USER_ID, ITEM_ID);
    }

    @Test
    void testGetItemById_notFound_throwsNotFoundException() throws Exception {
        when(itemService.getItemById(USER_ID, ITEM_ID))
                .thenThrow(new NotFoundException("Вещь не найдена"));

        mockMvc.perform(get("/items/{itemId}", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Вещь не найдена"));

        verify(itemService, times(1)).getItemById(USER_ID, ITEM_ID);
    }

    // --- GET /items ---

    @Test
    void testFindAllItems_success() throws Exception {
        ItemOwnerViewingDto itemDto = new ItemOwnerViewingDto();
        itemDto.setId(ITEM_ID);
        itemDto.setName("Drill");

        when(itemService.findAllItems(USER_ID)).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(ITEM_ID));

        verify(itemService, times(1)).findAllItems(USER_ID);
    }

    // --- GET /items/search ---

    @Test
    void testSearchItems_success() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(ITEM_ID);
        itemDto.setName("Drill");

        when(itemService.searchItems("Drill")).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"));

        verify(itemService, times(1)).searchItems("Drill");
    }

    @Test
    void testSearchItems_emptyQuery_returnsEmpty() throws Exception {
        when(itemService.searchItems("   ")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        verify(itemService, times(1)).searchItems("   ");
    }

    // --- POST /items/{itemId}/comment ---

    @Test
    void testCreateComment_success() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(COMMENT_ID);
        commentDto.setText("Great tool!");

        when(itemService.createComment(USER_ID, ITEM_ID, any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COMMENT_ID));

        verify(itemService, times(1)).createComment(USER_ID, ITEM_ID, any(CommentDto.class));
    }

    @Test
    void testCreateComment_userDidNotBook_throwsValidationException() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great tool!");

        when(itemService.createComment(USER_ID, ITEM_ID, any(CommentDto.class)))
                .thenThrow(new ValidationException("Пользователь не арендовал вещь"));

        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Пользователь не арендовал вещь"));

        verify(itemService, times(1)).createComment(USER_ID, ITEM_ID, any(CommentDto.class));
    }
}