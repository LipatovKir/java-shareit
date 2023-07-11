package ru.practicum.shareit.item.model;

import jdk.jfr.BooleanFlag;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "items", schema = "public")
public class Item {

    public static final String ITEM_ID_COLUMN = "item_id";
    public static final String REQUEST_ID_COLUMN = "request_id";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ITEM_ID_COLUMN)
    Long id;
    @NotBlank
    String name;
    @NotEmpty
    String description;
    @BooleanFlag()
    @NotNull
    Boolean available;
    @ManyToOne(fetch = FetchType.EAGER)
    User owner;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = REQUEST_ID_COLUMN)
    ItemRequest itemRequest;
    @OneToMany()
    @JoinColumn(name = ITEM_ID_COLUMN)
    List<Booking> bookings;
    @OneToMany()
    @JoinColumn(name = ITEM_ID_COLUMN)
    List<Comment> comments;

    public Item(Long id, String name, String description, Boolean available, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }

    public Item(String name, String description, Boolean available, User owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }

    public Item() {
    }
}
