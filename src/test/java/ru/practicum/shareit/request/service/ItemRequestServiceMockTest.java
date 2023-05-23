package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDb;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceMockTest {
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Mock
    private UserRepositoryDb userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User userA;

    private  User userB;

    private ItemRequest itemRequestA;

    private  ItemRequest itemRequestB;

    private  ItemRequest itemRequestIncorrect;

    private  ItemRequestDto itemRequestDtoA;

    private  ItemRequestDto itemRequestDtoB;

    private  ItemRequestDto itemRequestDtoIncorrect;

    @BeforeEach
    public  void setUp() {
        userA = User.builder().id(1)
                .email("a@caca.com")
                .name("A")
                .build();
        userB = User.builder().id(2)
                .email("b@caca.com")
                .name("B")
                .build();

        itemRequestA = ItemRequest.builder()
                .id(1)
                .description("boring")
                .requestor(userA)
                .created(LocalDateTime.of(2022, 2, 24, 5, 0, 1))
                .build();

        itemRequestB = ItemRequest.builder()
                .id(2)
                .description("appeal")
                .requestor(userB)
                .created(LocalDateTime.of(1989, 2, 24, 5, 0, 1))
                .build();

        itemRequestIncorrect = ItemRequest.builder()
                .id(1)
                .description("bad request")
                .requestor(userB)
                .build();
        itemRequestDtoA = ItemRequestMapper.makeItemRequestDto(itemRequestA);
        itemRequestDtoB = ItemRequestMapper.makeItemRequestDto(itemRequestB);
        itemRequestDtoIncorrect = ItemRequestMapper.makeItemRequestDto(itemRequestIncorrect);
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(userA));
    }


    @Test
    void addIncorrectItemRequestTest() {
        when(itemRequestRepository.save(any())).thenThrow(new RuntimeException("Error"));
        assertThrows(
                RuntimeException.class,
                () -> itemRequestService.addRequest(itemRequestDtoIncorrect, userB.getId()));
    }

    @Test
    void addItemRequestMockTest() {
        when(itemRequestRepository.save(any())).thenReturn(itemRequestA);
        ItemRequestDto itemRequest = itemRequestService.addRequest(itemRequestDtoA, userA.getId());
        assertEquals(itemRequest.getId(), itemRequestA.getId());
        assertEquals(itemRequest.getDescription(), itemRequestA.getDescription());
    }

    @Test
    void getItemRequestMockTest() {
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.ofNullable(itemRequestA));
        ItemRequestDto itemRequest = itemRequestService.getRequestById(userA.getId(), 1);
        assertEquals(itemRequest.getId(), itemRequestA.getId());
        assertEquals(itemRequest.getDescription(), itemRequestA.getDescription());
        assertEquals(itemRequest.getRequestorId(), itemRequestA.getRequestor().getId());
    }

    @Test
    void getAllRequestByUserMockTest() {
        when(itemRequestRepository.findAllByRequestor(any())).thenReturn(singletonList(itemRequestA));
        List<ItemRequestDto> itemRequest = itemRequestService.getAllRequestByUser(userA.getId());
        assertEquals(itemRequest.size(), 1);
        assertEquals(itemRequest.get(0).getId(), itemRequestA.getId());
        assertEquals(itemRequest.get(0).getDescription(), itemRequestA.getDescription());
        assertEquals(itemRequest.get(0).getRequestorId(), itemRequestA.getRequestor().getId());
    }

    @Test
    void getItemRequestFromSizeByOwnerTest() {
        Page<ItemRequest> page =  new PageImpl<>(Collections.singletonList(itemRequestA));

        when(itemRequestRepository.findAllByRequestorNot(any(), any())).thenReturn(page);
        var itemRequest = itemRequestService.getAllRequestsByPages(0, 1, userA.getId());
        assertEquals(itemRequest.size(), 1);
    }
}
