package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "items")
public class Item {

    public static final int MAX_DESCRIPTION_LENGTH = 512;
    public static final String NAME_COLUMN = "name";
    public static final String ID_COLUMN = "item_id";
    public static final String OWNER_COLUMN = "owner";
    public static final String AVAILABLE_COLUMN = "available";
    public static final String DESCRIPTION_COLUMN = "description";

    @Id
    @Column(name = ID_COLUMN)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = NAME_COLUMN, nullable = false)
    private String name;
    @Column(name = DESCRIPTION_COLUMN, nullable = false, length = MAX_DESCRIPTION_LENGTH)
    private String description;
    @Column(name = AVAILABLE_COLUMN, nullable = false)
    private Boolean available;
    @Column(name = OWNER_COLUMN, nullable = false)
    private Long owner;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id != null
                && Objects.equals(id, item.id)
                && Objects.equals(name, item.name)
                && Objects.equals(description, item.description)
                && Objects.equals(available, item.available)
                && Objects.equals(owner, item.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, owner);
    }
}
