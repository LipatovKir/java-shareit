package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.RequestError;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto addUser(User user) {
        UserDto userDto = UserMapper.toUserDto(userRepository.save(user));
        log.info("Добавлен пользователь с id:" + userDto.getId());
        return userDto;
    }

    @Override
    public UserDto patchUser(User user, Long userId) {
        //должна быть возможность заменить не только всего юзера но и почту либо имя у существующего
        Optional<User> userFromDbe = userRepository.findById(userId);
        if (userFromDbe.isPresent()) {
            if (user.getName() != null) {
                userFromDbe.get().setName(user.getName());
            }
            if (user.getEmail() != null) {
                userFromDbe.get().setEmail(user.getEmail());
            }
            userRepository.save(userFromDbe.get());
            log.info("Обновлен пользователь с id:" + userId);
            return UserMapper.toUserDto(userFromDbe.get());
        } else {
            throw new RequestError(HttpStatus.NOT_FOUND, "Taкого юзера нет в списке");
        }
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> UserMapper.toUserDto(user)).collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(Long id) {
        Optional<User> userFromDbe = userRepository.findById(id);
        if (userFromDbe.isPresent()) {
            return UserMapper.toUserDto(userFromDbe.get());
        } else {
            throw new RequestError(HttpStatus.NOT_FOUND, "Taкого юзера нет в списке");
        }
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
