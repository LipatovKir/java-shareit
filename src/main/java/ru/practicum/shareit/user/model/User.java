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

    public static final String ID_COLUMN = "id";
    public static final String REQUEST_ID_COLUMN = "request_id";
    public static final String NAME_COLUMN = "name";
    public static final String EMAIL_COLUMN = "email";
    public static final String AVAILABLE_COLUMN = "available";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID_COLUMN)
    Long id;
    @Column(name = NAME_COLUMN)
    String name;
    @Column(name = EMAIL_COLUMN)
    @Email
    @NotBlank
    String email;
    @OneToMany(mappedBy = "booker")
    List<Booking> bookings;
    @OneToMany(mappedBy = "owner")
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