package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {
    @Test
    void itemRequestFromDtoTest() {
        User user = User.builder()
                .id(1)
                .email("cucaracha@ya.com")
                .name("Nemo")
                .build();


        ItemRequestDto requestDto = ItemRequestDto.builder()
                .created(LocalDateTime.of(1992, 4, 22, 12, 12, 12))
                .description("moo")
                .id(1)
                .build();

        ItemRequest request = ItemRequestMapper.makeItemRequestFromDto(requestDto, user);

        ItemRequest requestTest = ItemRequest.builder()
                .created(LocalDateTime.of(1992, 4, 22, 12, 12, 12))
                .description("moo")
                .id(1)
                .requestor(user)
                .build();
       assertEquals(request.getId(),requestTest.getId());
       assertEquals(request.getRequestor(), requestTest.getRequestor());
       assertEquals(request.getDescription(), requestTest.getDescription());
       assertEquals(request.getCreated(), requestTest.getCreated());
    }

    @Test
    void itemRequestDtoFromItemRequestTest() {
        User user = User.builder()
                .id(1)
                .email("cucaracha@ya.com")
                .name("Nemo")
                .build();


        ItemRequestDto requestDtoTest = ItemRequestDto.builder()
                .created(LocalDateTime.of(1992, 4, 22, 12, 12, 12))
                .description("moo")
                .id(1)
                .requestorId(user.getId())
                .build();


        ItemRequest request = ItemRequest.builder()
                .created(LocalDateTime.of(1992, 4, 22, 12, 12, 12))
                .description("moo")
                .id(1)
                .requestor(user)
                .build();

        ItemRequestDto requestDto = ItemRequestMapper.makeItemRequestDto(request);
        assertEquals(requestDto.getId(),requestDtoTest.getId());
        assertEquals(requestDto.getRequestorId(), requestDtoTest.getRequestorId());
        assertEquals(requestDto.getDescription(), requestDtoTest.getDescription());
        assertEquals(requestDto.getCreated(), requestDtoTest.getCreated());
    }
    }
