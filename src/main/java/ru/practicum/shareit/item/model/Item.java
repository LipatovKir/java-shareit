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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    Long id;
    @NotBlank(message = "Имя не может быть пустым")
    String name;
    @NotEmpty(message = "Описание не может быть пустым")
    String description;
    //статус о том, доступна или нет вещь для аренды
    @BooleanFlag()
    @NotNull
    Boolean available;
    //владелец вещи
    @ManyToOne(fetch = FetchType.EAGER)
 //   @JoinColumn(name = "user_id")
    User owner;
    //если вещь была создана по запросу другого пользователя, то в этом поле будет храниться ссылка на соответствующий запрос
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID")
    ItemRequest itemRequest;
    @OneToMany()
    @JoinColumn(name = "item_id")
    List<Booking> bookings;
    @OneToMany()
    @JoinColumn(name = "item_id")
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
