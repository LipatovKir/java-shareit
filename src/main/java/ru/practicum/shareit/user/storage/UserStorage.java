package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    User updateUser(long userId, User user);

    User getUserById(long userId);

    void deleteUserById(long userId);

    List<User> getAllUsers();
}
