package ru.practicum.shareit.request.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Validated
public class ItemRequestController {
    ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestServiceImpl itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                            @Valid @RequestBody ItemRequestDto itemRequest) {
        log.info("Got Item Request");
        itemRequest = itemRequest.toBuilder().created(LocalDateTime.now()).build();
        return itemRequestService.addRequest(itemRequest, userId);

    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequestById(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                         @PathVariable int id) {
        log.info("Got request for item request");
        return itemRequestService.getRequestById(userId, id);
    }

    @GetMapping()
    public List<ItemRequestDto> getAllRequestsByUser(@RequestHeader(name = "X-Sharer-User-Id") int userId) {
        log.info("Got request for all item request of User with id:{}", userId);
        return itemRequestService.getAllRequestByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsByUserByPages(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                            @RequestParam(defaultValue = "1") @Positive int size) {
        log.info("Got request for all item request of User with id:{} in pages", userId);
        from = from / size;
        return itemRequestService.getAllRequestsByPages(from, size, userId);
    }
}
