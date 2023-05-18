package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
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
        itemRequest = itemRequest.toBuilder().created(LocalDateTime.now()).build();
        return itemRequestService.addRequest(itemRequest, userId);

    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequestById(@RequestHeader(name =  "X-Sharer-User-Id") int userId,
                                         @PathVariable int id) {
        return itemRequestService.getRequestById(userId, id);
    }

    @GetMapping()
    public List<ItemRequestDto> getAllRequestsByUser(@RequestHeader(name =  "X-Sharer-User-Id") int userId) {
        return itemRequestService.getAllRequestByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsByUserByPages(@RequestHeader(name =  "X-Sharer-User-Id") int userId,
                                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                            @RequestParam(defaultValue = "1") @Positive int size) {
        from = from / size;
        return itemRequestService.getAllRequestsByPages(from, size , userId);
    }
}
