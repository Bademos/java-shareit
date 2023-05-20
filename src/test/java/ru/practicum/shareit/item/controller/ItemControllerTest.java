package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentDtoMapper;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    private static String address;

    private static Item item;

    private static ItemDto itemDto;

    private static Item updateItem;

    private static User user;

    private static Comment comment;

    private static CommentDto commentDto;
    final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();


    @MockBean
    private ItemServiceImpl itemService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private ItemRequestServiceImpl itemRequestService;
    @Autowired
    private MockMvc mockMvc;
    @BeforeAll
    public static void beforeAll() {

        address = "/items";

        user = User.builder()
                .id(1)
                .name("Vadique")
                .email("vq@st.test")
                .build();

        item = Item.builder()
                .id(1)
                .name("Паяльник")
                .description("дешевый")
                .available(true)
                .owner(user)
                .build();
        itemDto = ItemDtoMapper.makeItemDto(item);
        updateItem = Item.builder()
                .id(1)
                .name("Паяльник")
                .description("Дорогой")
                .available(true)
                .owner(user)
                .build();

        comment = Comment.builder()
                .text("китайская подделка")
                .item(item)
                .user(user)
                .build();

        commentDto = CommentDtoMapper.makeCommentDto(comment);

    }

    @Test
    void createItemTest() {
        when(itemService.create(any())).thenReturn(item);

        try {
            mockMvc.perform(post(address)
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.name", is(item.getName())))
                    .andExpect(jsonPath("$.description", is(item.getDescription())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(itemService, times(1)).create(any());
    }

    @Test
    void createCommentTest() {
        when(userService.getById(anyInt())).thenReturn(user);
        when(itemService.getById(anyInt(),anyInt())).thenReturn(ItemDtoMapper.makeItemDto(item));
        when(itemService.createComment(any(), anyInt(), anyInt())).thenReturn(comment);

        try {
            mockMvc.perform(post(address + "/1/comment")
                            .content(mapper.writeValueAsString(commentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(itemService, times(1)).createComment(any(), anyInt(), anyInt());
    }

    @Test
    void updateTest() {
        when(itemService.update(anyInt(), any())).thenReturn(updateItem);

        try {
            mockMvc.perform(patch(address + "/1")
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.name", is(updateItem.getName())))
                    .andExpect(jsonPath("$.description", is(updateItem.getDescription())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(itemService, times(1)).update(anyInt(), any());
    }

    @Test
    void getByIdTest() {
        when(itemService.getById(anyInt(),anyInt())).thenReturn(itemDto);

        try {
            mockMvc.perform(get(address + "/1")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.name", is(item.getName())))
                    .andExpect(jsonPath("$.description", is(item.getDescription())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(itemService, times(1)).getById(anyInt(), anyInt());
    }

    @Test
    void getAllByUserTest() {
        when(itemService.getAllByUser(anyInt()))
                .thenReturn(Collections.singletonList(itemDto));

        try {
            mockMvc.perform(get(address)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$", hasSize(1)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(itemService, times(1))
                .getAllByUser(anyInt());
    }

    @Test
    void searchTest() {
        when(itemService.search( anyString()))
                .thenReturn(Collections.singletonList(item));

        try {
            mockMvc.perform(get(address + "/search")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .param("text", "Паяльник")
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$", hasSize(1)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(itemService, times(1))
                .search(anyString());
    }

}
