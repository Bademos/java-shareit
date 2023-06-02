package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
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

    private String address;

    private User userA;

    private UserDto userDtoA;

    private User updateUser;

    final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();


    @MockBean
    private final UserServiceImpl userService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeAll() {

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
    void addUser() throws Exception {
        when(userService.create(any())).thenReturn(userA);
        mockMvc.perform(post(address)
                            .content(mapper.writeValueAsString(userDtoA))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.name", is(userA.getName())))
                    .andExpect(jsonPath("$.email", is(userA.getEmail())));
        verify(userService, times(1)).create(any());
    }

    @Test
    void updateTest() throws Exception {
        when(userService.update(any())).thenReturn(updateUser);
        mockMvc.perform(patch(address + "/1")
                            .content(mapper.writeValueAsString(userDtoA))
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().is(200))
                    .andExpect(jsonPath("$.name", is(updateUser.getName())))
                    .andExpect(jsonPath("$.email", is(updateUser.getEmail())));

        verify(userService, times(1)).update(any());
    }


    @Test
    void getByIdTest() throws Exception {
        when(userService.getById(anyInt())).thenReturn(userA);

        mockMvc.perform(get(address + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().is(200))
                    .andExpect(jsonPath("$.name", is(userA.getName())))
                    .andExpect(jsonPath("$.email", is(userA.getEmail())));

        verify(userService, times(1)).getById(anyInt());
    }


    @Test
    void getAllTest() throws Exception {
        when(userService.getAll()).thenReturn(Collections.singletonList(userA));

        mockMvc.perform(get(address)
                    .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));


        verify(userService, times(1)).getAll();
    }

    @Test
    void deleteUser() throws Exception {
            mockMvc.perform(delete(address + "/1")
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().is(200));
    }
}
