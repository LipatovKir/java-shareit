package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(User user) {
        UserDto userDto = UserMapper.makeUserDto(userRepository.save(user));
        log.info("Добавлен пользователь с id:" + userDto.getId());
        return userDto;
    }

    @Override
    public UserDto updateUser(User user, Long userId) {
        Optional<User> userNew = userRepository.findById(userId);
        if (userNew.isPresent()) {
            if (user.getName() != null) {
                userNew.get().setName(user.getName());
            }
            if (user.getEmail() != null) {
                userNew.get().setEmail(user.getEmail());
            }
            userRepository.save(userNew.get());
            log.info("Обновлен пользователь с id:" + userId);
            return UserMapper.makeUserDto(userNew.get());
        } else {
            throw new UserNotFoundException("Пользователя нет в списке.");
        }
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::makeUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(Long id) {
        Optional<User> userFromDbe = userRepository.findById(id);
        if (userFromDbe.isPresent()) {
            return UserMapper.makeUserDto(userFromDbe.get());
        } else {
            throw new UserNotFoundException("Пользователя нет в списке.");
        }
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
