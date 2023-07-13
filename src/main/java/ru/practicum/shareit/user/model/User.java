package ru.practicum.shareit.user.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users", schema = "public")
public class User {

    public static final String USER_ID_COLUMN = "user_id";
    public static final String NAME_COLUMN = "name";
    public static final String EMAIL_COLUMN = "email";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = USER_ID_COLUMN)
    Long id;
    @Column(name = NAME_COLUMN, nullable = false)
    String name;
    @Email
    @NotBlank
    @Column(name = EMAIL_COLUMN, nullable = false, unique = true)
    String email;

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