package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    public static final String COMMENT_ID_COLUMN = "id";
    public static final String ITEM_ID_COLUMN = "item_id";
    public static final String AUTHOR_ID_COLUMN = "author_id";
    public static final String TEXT_COLUMN = "text";
    public static final String CREATED_COLUMN = "created";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COMMENT_ID_COLUMN)
    private Long id;
    @Column(name = TEXT_COLUMN)
    private String text;
    @Column(name = ITEM_ID_COLUMN)
    private Long itemId;
    @Column(name = AUTHOR_ID_COLUMN)
    private Long authorId;
    @Column(name = CREATED_COLUMN)
    private LocalDateTime created;
}