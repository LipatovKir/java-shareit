package ru.practicum.shareit.checkservice;

import org.springframework.data.domain.PageRequest;

public interface CheckService {

    void checkUser(Long userId);

    void checkItem(Long itemId);

    void checkBooking(Long booking);

    void checkRequest(Long requestId);

    PageRequest checkPageSize(Integer from, Integer size);
}
