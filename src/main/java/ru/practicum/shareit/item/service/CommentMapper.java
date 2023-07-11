package ru.practicum.shareit.item.service;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class CommentMapper {

    public static CommentDto commentDto(Comment comment, User author, Item item) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthor(author);
        commentDto.setItem(item);
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static CommentShortDto commentDtoToResponseDto(CommentDto commentDto) {
        CommentShortDto commentResponseDto = new CommentShortDto();
        commentResponseDto.setId(commentDto.getId());
        commentResponseDto.setText(commentDto.getText());
        commentResponseDto.setAuthorName(commentDto.getAuthor().getName());
        commentResponseDto.setCreated(commentDto.getCreated());
        return commentResponseDto;
    }
}