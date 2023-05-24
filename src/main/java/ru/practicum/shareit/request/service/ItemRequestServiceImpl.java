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
import ru.practicum.shareit.user.repository.UserRepositoryDb;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository requestRepository;
    UserRepositoryDb userRepository;

    public ItemRequestServiceImpl(ItemRequestRepository requestRepository, UserRepositoryDb userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemRequestDto addRequest(ItemRequestDto itemRequestDto, Integer userId) {
        User user = getAndCheckUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.makeItemRequestFromDto(itemRequestDto, user);
        itemRequest.toBuilder().created(LocalDateTime.now());
        return ItemRequestMapper.makeItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getRequestById(Integer userId, Integer integer) {
        getAndCheckUser(userId);
        ItemRequest result = requestRepository.findById(integer)
                .orElseThrow(() -> new NotFoundException("the request is absent"));
        return ItemRequestMapper.makeItemRequestDto(result);
    }

    @Override
    public List<ItemRequestDto> getAllRequestByUser(Integer userId) {
        User user = getAndCheckUser(userId);
        return requestRepository.findAllByRequestor(user)
                .stream()
                .map(ItemRequestMapper::makeItemRequestDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<ItemRequestDto> getAllRequestsByPages(Integer from, Integer size, Integer userId) {
        from = from / size;
        User user = getAndCheckUser(userId);
        return requestRepository.findAllByRequestorNot(user, PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "created")))
                .getContent()
                .stream()
                .map(ItemRequestMapper::makeItemRequestDto)
                .collect(Collectors.toList());
    }

    private User getAndCheckUser(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("The user is absent"));
    }
}