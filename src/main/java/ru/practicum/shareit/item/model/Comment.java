package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "comments")
public class Comment {

    public static final int MAX_TEXT_LENGTH = 512;
    public static final String TEXT_COLUMN_NAME = "text";
    public static final String ITEM_COLUMN_NAME = "item_id";
    public static final String AUTHOR_COLUMN_NAME = "author_id";
    public static final String ID_COLUMN_NAME = "comment_id";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = TEXT_COLUMN_NAME, nullable = false, length = MAX_TEXT_LENGTH)
    private String text;
    @ManyToOne
    @JoinColumn(name = ITEM_COLUMN_NAME)
    private Item item;
    @ManyToOne
    @JoinColumn(name = AUTHOR_COLUMN_NAME)
    private User author;
    @NotNull
    private LocalDateTime created;
}

