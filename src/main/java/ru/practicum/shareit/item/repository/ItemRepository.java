package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(long userId);

    List<Item> findByRequestId(long requestId);

    List<Item> findByOwnerId(long userId, PageRequest pageRequest);

    @Query("select i from Item i where upper(i.name) like upper(concat('%', ?1, '%')) or upper(i.description) " +
            "like upper(concat('%', ?1, '%')) and i.available = true ")
    List<Item> search(String text, PageRequest pageRequest);
}





