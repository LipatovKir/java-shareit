package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select r from ItemRequest r " +
            "where r.requester.id = ?1 order by r.created desc ")
    List<ItemRequest> findRequestsByUser(Long userId);

    @Query("select r from ItemRequest r " +
            "where r.requester.id <> ?1 order by r.created desc ")
    Page<ItemRequest> findRequestsWithoutOwner(Long userId, PageRequest pageRequest);
}
