package ru.practicum.shareit.test.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.test.constants.Constants.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    Item firstitem;
    Item secondItem;
    User user;

    @BeforeEach
    void beforeEach() {

        user = userRepository.save(User.builder()
                .id(1L)
                .name(TEST_USER)
                .email(TEST_EMAIL)
                .build());

        firstitem = itemRepository.save(Item.builder()
                .name(TEST_ITEM)
                .description(TEST_ITEM_DESCRIPTION)
                .available(true)
                .owner(user)
                .build());

        secondItem = itemRepository.save(Item.builder()
                .name(TEST_ITEM_SECOND)
                .description(TEST_ITEM_DESCRIPTION_SECOND)
                .available(true)
                .owner(user)
                .build());
    }

    @Test
    void search() {
        List<Item> items = itemRepository.search("2л", PageRequest.of(0, 1));
        assertEquals(1, items.size());
        assertEquals("электрочайник", items.get(0).getName());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }
}