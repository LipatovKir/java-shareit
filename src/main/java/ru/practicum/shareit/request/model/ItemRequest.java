package ru.practicum.shareit.request.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "requests", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    public static final String REQUEST_COLUMN = "request_id";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String CREATED_COLUMN = "created";
    public static final String REQUESTOR_COLUMN = "requestor_id";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = REQUEST_COLUMN)
    Long id;
    @NotBlank
    @Column(name = DESCRIPTION_COLUMN)
    String description;
    @ManyToOne(fetch = FetchType.EAGER)
  //  @Column(name = REQUESTOR_COLUMN)
    User requester;
    @Column(name = CREATED_COLUMN)
    LocalDateTime created;
    @OneToMany(mappedBy = "itemRequest")
    List<Item> items = new ArrayList<>();
}
