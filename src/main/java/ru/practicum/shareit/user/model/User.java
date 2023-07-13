package ru.practicum.shareit.user.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "users",
        schema = "public",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"email"})
)
public class User {

    public static final String USER_ID_COLUMN = "user_id";
    public static final String BOOKER = "booker";
    public static final String OWNER = "owner";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = USER_ID_COLUMN)
    Long id;
    String name;
    @Email
    @NotBlank
    String email;
    @OneToMany(mappedBy = BOOKER)
    List<Booking> bookings;
    @OneToMany(mappedBy = OWNER)
    List<Item> items;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User() {
    }
}