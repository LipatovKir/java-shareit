package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation_label.Create;
import ru.practicum.shareit.validation_label.Update;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid
                              @Validated({Create.class})
                              @RequestBody UserDto userDto) {
        User user = UserMapper.makeDtoToUser(userDto);
        return userService.createUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") Long userId,
                              @Validated({Update.class})
                              @RequestBody UserDto userDto) {
        User user = UserMapper.makeDtoToUser(userDto);
        return userService.updateUser(user, userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {

        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") Long id) {

        return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);
    }
}
