package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService service;

    public ItemController(ItemServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(name = "X-Sharer-User-Id") int userId) {
        log.info("Got request for items of user with i: {}", userId);
        return service.getAllByUser(userId).stream().map(ItemDtoMapper::makeItemDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDto getById(@RequestHeader(name = "X-Sharer-User-Id") int userId, @PathVariable int id) {
        log.info("Got request for item  with id {}", id);
        return ItemDtoMapper.makeItemDto(service.getById(id));
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        log.info("Got request from user with id {} to create item", userId);
        Item item = ItemDtoMapper.makeItemFromDto(itemDto, userId);
        Item itemCreated = service.create(item);
        return ItemDtoMapper.makeItemDto(itemCreated);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                          @PathVariable int id, @RequestBody ItemDto itemDto) {
        log.info("Got request from user with id:{} to update item with id:{}", userId, id);
        Item item = ItemDtoMapper.makeItemFromDto(itemDto, userId);
        return ItemDtoMapper.makeItemDto(service.update(id, item));
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                       @PathVariable int id) {
        log.info("Got request from user with id: {} to delete item with id: {}", userId, id);
        service.removeItem(id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByRequest(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                         @RequestParam(defaultValue = "") String text) {

        log.info("Got request from user with id: {} to search items contains the text: {}", userId, text);
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return service.search(text).stream().map(ItemDtoMapper::makeItemDto).collect(Collectors.toList());
    }
}