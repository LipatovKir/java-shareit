package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.checkservice.CheckService;
import ru.practicum.shareit.user.exception.EmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String USER_NOT_FOUND = "Пользователь не найден";
    private final UserRepository userRepository;
    private final CheckService checkService;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.makeDtoInUser(userDto);
        userRepository.save(user);
        return UserMapper.makeUserInDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        checkService.checkUser(userId);
        User user = UserMapper.makeDtoInUser(userDto);
        user.setId(userId);
        return userRepository.findById(userId).map(newUser -> {
            if (user.getName() != null) {
                newUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                List<User> findEmail = userRepository.findByEmail(user.getEmail());
                if (!findEmail.isEmpty() && findEmail.get(0).getId() != userId) {
                    throw new EmailException("Уже зарегистрирован пользователь с email " + user.getEmail());
                }
                newUser.setEmail(user.getEmail());
            }
            userRepository.save(newUser);
            return UserMapper.makeUserInDto(newUser);
        }).orElse(null);
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        checkService.checkUser(userId);
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long userId) {
        checkService.checkUser(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));
        return UserMapper.makeUserInDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.makeUserDtoList(userRepository.findAll());
    }
}