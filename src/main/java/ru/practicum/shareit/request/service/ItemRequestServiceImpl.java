package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository requestRepository;
    UserService userService;

    public ItemRequestServiceImpl(ItemRequestRepository requestRepository, UserServiceImpl userService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
    }

    @Override
    public ItemRequestDto addRequest(ItemRequestDto itemRequestDto, Integer userId) {
        User user = userService.getById(userId);
        ItemRequest itemRequest = ItemRequestMapper.makeItemRequestFromDto(itemRequestDto, user);
        itemRequest.toBuilder().created(LocalDateTime.now());
        return ItemRequestMapper.makeItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getRequestById(Integer userId, Integer integer) {
        userService.getById(userId);
        ItemRequest result = requestRepository.findById(integer)
                .orElseThrow(() -> new NotFoundException("the request is absent"));
        return ItemRequestMapper.makeItemRequestDto(result);
    }

    @Override
    public List<ItemRequestDto> getAllRequestByUser(Integer userId) {
        User user = userService.getById(userId);
        return requestRepository.findAllByRequestor(user)
                .stream()
                .map(ItemRequestMapper::makeItemRequestDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<ItemRequestDto> getAllRequestsByPages(Integer from, Integer size, Integer userId) {
        User user = userService.getById(userId);
        return requestRepository.findAllByRequestorNot(user, PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "created")))
                .getContent()
                .stream()
                .map(ItemRequestMapper::makeItemRequestDto)
                .collect(Collectors.toList());
    }
}