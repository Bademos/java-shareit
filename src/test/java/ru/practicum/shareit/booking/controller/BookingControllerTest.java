package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.TimeIntervalException;
import ru.practicum.shareit.item.dto.item.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(BookingController.class)

public class BookingControllerTest {
    private String address;

    private Booking booking;

    private BookingDtoOut bookingDto;
    private BookingDto bookingDt;
    private final static LocalDateTime startDate =  LocalDateTime.of(2024,1,11,11,11,11);
    private final static LocalDateTime endDate =  LocalDateTime.of(2024,2,22,22,22,22);

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        address = "/bookings";

        booking = Booking.builder()
                .id(1)
                .startBooking(startDate)
                .endBooking(endDate)
                .item(Item.builder().id(1).build())
                .user(User.builder().id(1).build())
                .status(BookingStatus.WAITING)
                .build();

        bookingDt = BookingDto.builder()
                .itemId(1)
                .start(startDate)
                .end(endDate)
                .build();

        bookingDto = BookingDtoOut.builder()
                .id(1)
                .start(startDate)
                .end(endDate)
                .item(ItemDtoOut.builder().id(1).build())
                .booker(null)
                .status(BookingStatus.REQUESTED)
                .build();
        bookingDto = BookingDtoMapper.makeBookingDtoOutFromBooking(booking);
    }

    @Test
    void createBookingTest() {
        when(bookingService.createBooking(any(), anyInt())).thenReturn(bookingDto);

        try {
            mockMvc.perform(post(address)
                            .content(mapper.writeValueAsString(bookingDt))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(jsonPath("$.id", is(bookingDto.getId())))
                    .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                    .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(bookingService, times(1)).createBooking(any(), anyInt());
    }

    @Test
    void updateBookingByOtherUserTest() throws Exception {
        when(bookingService.updateBooking(anyInt(), anyBoolean(), anyInt()))
                .thenThrow(NotFoundException.class);
        mockMvc.perform(patch(address + "/1")
                            .content(mapper.writeValueAsString(bookingDt))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(400));
    }

    @Test
    void getBookingsByUserTest() throws Exception {
        when(bookingService.getBookingByUser(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingDto));
        mockMvc.perform(get(address)
                            .content(mapper.writeValueAsString(bookingDt))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService, times(1))
                .getBookingByUser(anyInt(), any(), anyInt(), anyInt());
    }

    @Test
    void getBookingsByUserUnsupportedStatusTest() {
        try {
            mockMvc.perform(get(address)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("state","unsup")
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(500));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(bookingService, times(0))
                .getBookingByUser(anyInt(), any(), anyInt(), anyInt());
    }

    @Test
    void getBookingsByOwnerTest() throws Exception {
        when(bookingService.getBookingForAllItemsByUser(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingDto));

        mockMvc.perform(get(address + "/owner")
                            .content(mapper.writeValueAsString(bookingDt))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$", hasSize(1)));
        verify(bookingService, times(1))
                .getBookingForAllItemsByUser(anyInt(), any(), anyInt(), anyInt());
    }

    @Test
    void getBookingsByOwnerWithWrongLimitsTest() throws Exception {
            mockMvc.perform(get(address + "/owner")
                            .content(mapper.writeValueAsString(bookingDt))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .param("from", "-1")
                            .param("size", "1")
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(400));
        verify(bookingService, times(0))
                .getBookingForAllItemsByUser(anyInt(), any(), anyInt(), anyInt());
    }

    @Test
    void addBookingWithIntervalErrorTest() throws Exception {
        when(bookingService.createBooking(any(), anyInt())).thenThrow(TimeIntervalException.class);
        mockMvc.perform(post(address)
                            .content(mapper.writeValueAsString(bookingDt))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(400));

        verify(bookingService, times(1)).createBooking(any(), anyInt());
    }

    @Test
    void approveBookingTest() throws Exception {
        BookingDtoOut bookingDtoOutResp = BookingDtoOut.builder()
                                        .id(1).status(BookingStatus.APPROVED).build();
        when(bookingService.updateBooking(anyInt(),anyBoolean(), anyInt())).thenReturn(bookingDtoOutResp);
        mockMvc.perform(patch(address + "/1")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .param("approved", "1")
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200));
        verify(bookingService, times(1)).updateBooking(anyInt(),anyBoolean(), anyInt());
    }

    @Test
    void getBookingTest() throws Exception {
        when(bookingService.getBookingById(anyInt(),anyInt())).thenReturn(bookingDto);
        mockMvc.perform(get(address + "/1")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200));
    }
}

