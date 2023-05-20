package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    private static String address;

    private static User userA;

    private static UserDto userDtoA;

    private static User updateUser;

    final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();


    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private ItemServiceImpl itemService;

    @MockBean
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void beforeAll() {

        address = "/users";

        userA = User.builder()
                .id(1)
                .name("user")
                .email("user@user.com")
                .build();
        userDtoA = UserDtoMapper.makeUserDto(userA);


        updateUser = User.builder()
                .id(1)
                .name("updateuser")
                .email("user@user.com")
                .build();

    }

    @Test
    void addUser() {
        when(userService.create(any())).thenReturn(userA);

        try {
            mockMvc.perform(post(address)
                            .content(mapper.writeValueAsString(userDtoA))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.name", is(userA.getName())))
                    .andExpect(jsonPath("$.email", is(userA.getEmail())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(userService, times(1)).create(any());
    }

    @Test
    void addUserWithoutEmail() {
        UserDto userWithoutEmail = UserDto.builder().name("user").build();
        try {
            mockMvc.perform(post(address)
                    .content(mapper.writeValueAsString(userWithoutEmail))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().is(400));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(userService, times(0)).create(any());
    }

    @Test
    void addUserWithIncorrectEmail() {
        UserDto userWithInvalidMail = UserDto.builder()
                .email("lib.ru").name("user").build();

        try {
            mockMvc.perform(post(address)
                    .content(mapper.writeValueAsString(userWithInvalidMail))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().is(400));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(userService, times(0)).create(any());
    }

    @Test
    void updateTest() {
        when(userService.update(any())).thenReturn(updateUser);

        try {
            mockMvc.perform(patch(address + "/1")
                            .content(mapper.writeValueAsString(userDtoA))
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().is(200))
                    .andExpect(jsonPath("$.name", is(updateUser.getName())))
                    .andExpect(jsonPath("$.email", is(updateUser.getEmail())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(userService, times(1)).update(any());
    }


    @Test
    void getByIdTest() {
        when(userService.getById(anyInt())).thenReturn(userA);

        try {
            mockMvc.perform(get(address + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().is(200))
                    .andExpect(jsonPath("$.name", is(userA.getName())))
                    .andExpect(jsonPath("$.email", is(userA.getEmail())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(userService, times(1)).getById(anyInt());
    }


    @Test
    void getAllTest() {
        when(userService.getAll()).thenReturn(Collections.singletonList(userA));

        try {
            mockMvc.perform(get(address)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().is(200));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(userService, times(1)).getAll();
    }
}
