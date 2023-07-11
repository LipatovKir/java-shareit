package ru.practicum.shareit.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepoTest {

    Long id;
    List<Long> ids;
    User userTest;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void createUser() {

        userTest = new User(id, "Test", "test@test.com");
        userRepository.save(userTest);
        List<User> allUsers = userRepository.findAll();
        assertEquals(1, allUsers.size());
        id = allUsers.get(0).getId();
        ids = List.of(id);
    }

    @Test
    void findByUserIds() {
        List<User> users = userRepository.findByUserIds(ids);
        assertEquals(1, users.size());
        assertEquals(id, users.get(0).getId());
        assertEquals("Test", users.get(0).getName());
        assertEquals("test@test.com", users.get(0).getEmail());
    }

    @AfterEach
    void deleteUsers() {
        userRepository.deleteAll();
    }
}