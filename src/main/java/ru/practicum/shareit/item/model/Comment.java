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
@Table(name = "comments", schema = "public")
public class Comment {

    public static final String COMMENT_ID_COLUMN = "id";
    public static final String ITEM_ID_COLUMN = "item_id";
    public static final String AUTHOR_ID_COLUMN = "author";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COMMENT_ID_COLUMN)
    private Long id;
    private String text;
    @Column(name = ITEM_ID_COLUMN)
    private Long itemId;
    @Column(name = AUTHOR_ID_COLUMN)
    private Long authorId;
    private LocalDateTime created;
}