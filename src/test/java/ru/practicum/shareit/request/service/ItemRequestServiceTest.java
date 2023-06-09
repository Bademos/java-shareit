package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDb;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class ItemRequestServiceTest {
    @Autowired
    ItemRequestServiceImpl itemRequestService;

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepositoryDb userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private static User userA;

    private static User userB;

    private static ItemRequest itemRequestA;

    private static ItemRequest itemRequestB;

    private static ItemRequest itemRequestIncorrect;

    private static ItemRequestDto itemRequestDtoA;

    private static ItemRequestDto itemRequestDtoB;

    private static ItemRequestDto itemRequestDtoIncorrect;

    @BeforeAll
    public static void beforeAll() {
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

    }

    @BeforeEach
    public void beforeEach() {
        userService.create(userA);
    }

    @Test
    void addIncorrectItemRequestTest() {
        assertThrows(
                RuntimeException.class,
                () -> itemRequestService.addRequest(itemRequestDtoIncorrect, userB.getId()));
    }

    @Test
    void addItemRequestTest() {
        ItemRequestDto itemRequest = itemRequestService.addRequest(itemRequestDtoA, userA.getId());

        assertEquals(itemRequest.getId(), itemRequestA.getId());
        assertEquals(itemRequest.getDescription(), itemRequestA.getDescription());
    }

    @Test
    void getItemRequest() {
        itemRequestService.addRequest(itemRequestDtoA, userA.getId());
        ItemRequestDto itemRequest = itemRequestService.getRequestById(userA.getId(), 1);
        assertEquals(itemRequest.getId(), itemRequestA.getId());
        assertEquals(itemRequest.getDescription(), itemRequestA.getDescription());
        assertEquals(itemRequest.getRequestorId(), itemRequestA.getRequestor().getId());
    }

    @Test
    void getAllRequestByUserTest() {
        ItemRequestDto itemRequestDto = itemRequestService.addRequest(itemRequestDtoA, userA.getId());
        List<ItemRequestDto> itemRequest = itemRequestService.getAllRequestByUser(userA.getId());
        assertEquals(itemRequest.size(), 1);
        assertEquals(itemRequest.get(0).getId(), itemRequestA.getId());
        assertEquals(itemRequest.get(0).getDescription(), itemRequestA.getDescription());
        assertEquals(itemRequest.get(0).getRequestorId(), itemRequestA.getRequestor().getId());
    }

    @Test
    void getItemRequestFromSizeTest() {
        userService.create(userB);
        itemRequestService.addRequest(itemRequestDtoA, userA.getId());
        List<ItemRequestDto> itemRequest = itemRequestService.getAllRequestsByPages(0, 1, userB.getId());
        assertEquals(itemRequest.size(), 1);
        assertEquals(itemRequest.get(0).getId(), itemRequestA.getId());
        assertEquals(itemRequest.get(0).getDescription(), itemRequestA.getDescription());
        assertEquals(itemRequest.get(0).getRequestorId(), itemRequestA.getRequestor().getId());
    }

    @Test
    void getItemRequestFromSizeByOwnerTest() {
        itemRequestService.addRequest(itemRequestDtoA, userA.getId());
        var itemRequest = itemRequestService.getAllRequestsByPages(0, 1, userA.getId());
        assertEquals(itemRequest.size(), 0);
    }
}
