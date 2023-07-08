package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.makeModel(userDto, null);
        user = userRepository.save(user);
        return UserMapper.makeDto(user);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = patchUser(userId, userDto);
        user = userRepository.save(user);
        return UserMapper.makeDto(user);
    }

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.makeDto(userRepository.findById(userId).orElseThrow());
    }

    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.makeUserListToUserDtoList(userRepository.findAll());
    }

    private User patchUser(Long userId, UserDto patch) {
        UserDto dto = getUserById(userId);
        String name = patch.getName();
        if (name != null && !name.isBlank()) {
            dto.setName(name);
        }
        String oldEmail = dto.getEmail();
        String newEmail = patch.getEmail();
        if (newEmail != null && !newEmail.isBlank() && !oldEmail.equals(newEmail)) {
            dto.setEmail(newEmail);
        }
        return UserMapper.makeModel(dto, userId);
    }
}
