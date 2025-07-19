package ru.practicum.shareit.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.ItemController;
import ru.practicum.shareit.server.item.dto.*;
import ru.practicum.shareit.server.item.ItemService;

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

    private static NewItemDto newItemDto;
    private static ItemDto itemDto;
    private static ItemPatchDto itemPatchDto;
    private static ItemViewingDto itemViewingDto;
    private static ItemOwnerViewingDto itemOwnerViewingDto;
    private static CommentDto commentDto;

    @BeforeAll
    static void setup() {
        newItemDto = NewItemDto.builder()
                .name("Saw")
                .description("Hand saw")
                .available(true)
                .owner(1)
                .build();
        itemDto = ItemDto.builder()
                .id(ITEM_ID)
                .name("Drill")
                .available(true)
                .build();
        itemPatchDto = ItemPatchDto.builder()
                .name("Improved Drill")
                .description("Even more powerful")
                .build();
        itemViewingDto = ItemViewingDto.builder()
                .id(ITEM_ID)
                .name("Drill")
                .comments(List.of(1L))
                .build();
        itemOwnerViewingDto = ItemOwnerViewingDto.builder()
                .name("Drill")
                .build();
        commentDto = CommentDto.builder()
                .id(COMMENT_ID)
                .text("Great tool!")
                .build();

    }

    @Test
    void testCreateItem_success() throws Exception {
        itemDto.setName("Drill");
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


        when(itemService.createItem(USER_ID, newItemDto))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).createItem(USER_ID, newItemDto);
    }

    @Test
    void testUpdateItem_success() throws Exception {
        itemPatchDto.setName("Updated Drill");

        itemDto.setId(ITEM_ID);
        itemDto.setName("Updated Drill");
        itemDto.setAvailable(true);

        when(itemService.updateItem(USER_ID, ITEM_ID, itemPatchDto)).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemPatchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Drill"));

        verify(itemService, times(1)).updateItem(USER_ID, ITEM_ID, itemPatchDto);
    }

    //
    @Test
    void testUpdateItem_notOwner_throwsNotFoundException() throws Exception {

        when(itemService.updateItem(USER_ID, ITEM_ID, itemPatchDto))
                .thenThrow(new NotFoundException("Пользователь не владелец"));

        mockMvc.perform(patch("/items/{itemId}", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemPatchDto)))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).updateItem(USER_ID, ITEM_ID, itemPatchDto);
    }

    @Test
    void testGetItemById_success() throws Exception {
        when(itemService.getItemById(USER_ID, ITEM_ID)).thenReturn(itemViewingDto);

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
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).getItemById(USER_ID, ITEM_ID);
    }

    @Test
    void testFindAllItems_success() throws Exception {
        when(itemService.findAllItems(USER_ID)).thenReturn(List.of(itemOwnerViewingDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"));

        verify(itemService, times(1)).findAllItems(USER_ID);
    }

    @Test
    void testSearchItems_success() throws Exception {
        itemDto.setName("Drill");

        when(itemService.searchItems("Drill")).thenReturn(List.of(itemDto));

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

    @Test
    void testCreateComment_success() throws Exception {

        when(itemService.createComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COMMENT_ID));

        verify(itemService, times(1)).createComment(anyLong(), anyLong(),
                any(CommentDto.class));
    }

    @Test
    void testCreateComment_userDidNotBook_throwsValidationException() throws Exception {

        when(itemService.createComment(anyLong(), anyLong(),
                any(CommentDto.class)))
                .thenThrow(new ValidationException("Пользователь не арендовал вещь"));

        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, times(1)).createComment(anyLong(), anyLong(),
                any(CommentDto.class));
    }
}