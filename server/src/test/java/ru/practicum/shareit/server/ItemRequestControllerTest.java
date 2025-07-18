package ru.practicum.shareit.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.request.ItemRequestController;
import ru.practicum.shareit.server.request.ItemRequestService;
import ru.practicum.shareit.server.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.NewItemRequestDto;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final long USER_ID = 1L;
    private static final long REQUEST_ID = 100L;

    // --- POST /requests ---

    @Test
    void testCreateItemRequest_success() throws Exception {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setDescription("Need a drill");
        newItemRequestDto.setRequestId(1L);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(REQUEST_ID);
        itemRequestDto.setDescription("Need a drill");
        itemRequestDto.setCreated(LocalDateTime.now());

        when(itemRequestService.createItemRequest(USER_ID, newItemRequestDto)).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.description").value("Need a drill"));

        verify(itemRequestService, times(1)).createItemRequest(USER_ID, newItemRequestDto);
    }

    @Test
    void testCreateItemRequest_userNotFound_throwsNotFoundException() throws Exception {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setDescription("Need a drill");

        when(itemRequestService.createItemRequest(USER_ID, newItemRequestDto))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользователь не найден"));

        verify(itemRequestService, times(1)).createItemRequest(USER_ID, newItemRequestDto);
    }

    // --- GET /requests ---

    @Test
    void testFindAllByOwnerRequests_success() throws Exception {
        ItemRequestAnswerDto answerDto = new ItemRequestAnswerDto();
        answerDto.setId(REQUEST_ID);
        answerDto.setDescription("Need a drill");
        answerDto.setCreated(LocalDateTime.now());

        when(itemRequestService.findAllByOwnerRequests(USER_ID)).thenReturn(List.of(answerDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(REQUEST_ID))
                .andExpect(jsonPath("$[0].description").value("Need a drill"));

        verify(itemRequestService, times(1)).findAllByOwnerRequests(USER_ID);
    }

    // --- GET /requests/all ---

    @Test
    void testFindAllRequests_success() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(REQUEST_ID);
        itemRequestDto.setDescription("Another request");
        itemRequestDto.setCreated(LocalDateTime.now());

        when(itemRequestService.findAllRequests(USER_ID)).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(REQUEST_ID));

        verify(itemRequestService, times(1)).findAllRequests(USER_ID);
    }

    // --- GET /requests/{requestId} ---

    @Test
    void testGetRequestById_success() throws Exception {
        ItemRequestAnswerDto answerDto = new ItemRequestAnswerDto();
        answerDto.setId(REQUEST_ID);
        answerDto.setDescription("Need a drill");
        answerDto.setCreated(LocalDateTime.now());

        when(itemRequestService.getRequestById(USER_ID, REQUEST_ID)).thenReturn(answerDto);

        mockMvc.perform(get("/requests/{requestId}", REQUEST_ID)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.description").value("Need a drill"));

        verify(itemRequestService, times(1)).getRequestById(USER_ID, REQUEST_ID);
    }

    @Test
    void testGetRequestById_notFound_throwsNotFoundException() throws Exception {
        when(itemRequestService.getRequestById(USER_ID, REQUEST_ID))
                .thenThrow(new NotFoundException("Запрос не найден"));

        mockMvc.perform(get("/requests/{requestId}", REQUEST_ID)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Запрос не найден"));

        verify(itemRequestService, times(1)).getRequestById(USER_ID, REQUEST_ID);
    }

    // --- Вспомогательные методы ---

    @Test
    void testFindAllRequests_userNotFound_throwsNotFoundException() throws Exception {
        when(itemRequestService.findAllRequests(USER_ID))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользователь не найден"));

        verify(itemRequestService, times(1)).findAllRequests(USER_ID);
    }
}
