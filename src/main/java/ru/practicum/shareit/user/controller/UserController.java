package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation_label.Create;
import ru.practicum.shareit.validation_label.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    public static final int VALID_ID = 1;
    public static final String USER_ID_ERROR = "ID пользователя не может быть NULL ";

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@NotNull(message = (USER_ID_ERROR))
                                @Min(VALID_ID)
                                @PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@NotNull(message = USER_ID_ERROR)
                              @Min(VALID_ID)
                              @PathVariable Long userId,
                              @Validated({Update.class})
                              @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@NotNull(message = (USER_ID_ERROR))
                               @Min(VALID_ID)
                               @PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}

