package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    private static String address;
    private static User user;
    private static UserDto userDto;
    private static ItemRequest itemRequest;

    private static ItemRequestDto itemRequestDto;
    private static ItemRequest itemRequestB;

    private static ItemRequestDto itemRequestDtoB;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestServiceImpl itemRequestService;

    @MockBean
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    @BeforeAll
    public static void beforeAll() {
        address = "/requests";

        user = User.builder().id(1)
                .email("a@caca.com")
                .name("A")
                .build();
        userDto = UserDtoMapper.makeUserDto(user);

        itemRequest = ItemRequest.builder()
                .id(1)
                .description("Нужна паяльная лампа")
                .requestor(user)
                .created(LocalDateTime.of(2022, 2, 24, 5, 0, 1))
                .build();
        itemRequestDto = ItemRequestMapper.makeItemRequestDto(itemRequest);

        itemRequestB = ItemRequest.builder()
                .id(1)
                .requestor(user)
                .created(LocalDateTime.of(2022, 2, 24, 5, 0, 1))
                .build();
        itemRequestDtoB = ItemRequestMapper.makeItemRequestDto(itemRequestB);
    }

    @Test
    void addIRTest() throws Exception {
        when(itemRequestService.addRequest(any(), any()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post(address)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
        verify(itemRequestService, times(1)).addRequest(any(),any());
    }

    @Test
    void addIncorrectItemRequestTest() throws Exception {
        try {
            mockMvc.perform(post(address)
                    .content(mapper.writeValueAsString(itemRequestDtoB))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", 1)
            ).andExpect(status().is(400));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

        @Test
    void getRequestByIdTest() throws Exception {
        when(itemRequestService.getRequestById(any(),any())).thenReturn(itemRequestDto);
        mockMvc.perform(get(address + "/1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    verify(itemRequestService,times(1)).getRequestById(any(), any());
    }

    @Test
    void getAllRequestByUserIdTest() throws Exception {
        when(itemRequestService.getAllRequestByUser(any())).thenReturn(Collections.singletonList(itemRequestDto));
            mockMvc.perform(post(address)
                    .content(mapper.writeValueAsString(itemRequestDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", 1)
                    ).andExpect(status().is(200));

                mockMvc.perform(get(address)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.length()").value(1));
        verify(itemRequestService,times(1)).getAllRequestByUser(any());
    }

    @Test
    void addRequestByPagesTest() {
        when(itemRequestService.getAllRequestsByPages(anyInt(), anyInt(), anyInt())).thenReturn(Collections.singletonList(itemRequestDto));
        try {
            mockMvc.perform(get(address + "/all")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.length()").value(1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(itemRequestService,times(1)).getAllRequestsByPages(anyInt(), anyInt(), anyInt());
    }
}
