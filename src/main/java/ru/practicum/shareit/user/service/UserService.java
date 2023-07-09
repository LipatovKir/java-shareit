package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto addUser(User user);

    UserDto patchUser(User user, Long userId);

    List<UserDto> getUsers();

    UserDto findUserById(Long id);

    void deleteUser(Long id);
}
