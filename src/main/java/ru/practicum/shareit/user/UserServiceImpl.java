package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.checkservice.CheckService;
import ru.practicum.shareit.user.exception.EmailException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {

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
        User user = UserMapper.makeDtoInUser(userDto);
        user.setId(userId);
        checkService.checkUser(userId);
        if (userRepository.findById(userId).isPresent()) {
            User newUser = userRepository.findById(userId).get();
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
        }
        return null;
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
        return UserMapper.makeUserInDto(userRepository.findById(userId).get());
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.makeUserDtoList(userRepository.findAll());
    }
}