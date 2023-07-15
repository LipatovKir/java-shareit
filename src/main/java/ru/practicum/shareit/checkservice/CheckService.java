package ru.practicum.shareit.checkservice;

public interface CheckService {

    void checkUser(Long userId);

    void checkItem(Long itemId);

    void checkBooking(Long booking);

}
