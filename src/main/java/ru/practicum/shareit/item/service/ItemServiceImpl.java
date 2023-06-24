package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final String OWNER_NOT_FOUND = "Не найден владелец c id: ";
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item createItem(Item item) {
        boolean ownerExists = isOwnerExists(item.getOwner());
        if (ownerExists) {
            itemStorage.createItem(item);
        }
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        return itemStorage.updateItem(item);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemStorage.getItemById(itemId);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        return itemStorage.getAllItems(userId);
    }

    @Override
    public List<Item> getItemsByRequest(String text) {
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        return itemStorage.getItemsByRequest(text);
    }

    private boolean isOwnerExists(long ownerId) {
        List<User> users = userStorage.getAllUsers();
        List<User> result = users.stream()
                .filter(user -> user.getId() == ownerId)
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new OwnerNotFoundException(OWNER_NOT_FOUND + ownerId);
        }
        return true;
    }
}
