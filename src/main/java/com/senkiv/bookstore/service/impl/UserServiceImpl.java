package com.senkiv.bookstore.service.impl;

import com.senkiv.bookstore.dto.UserRegistrationRequestDto;
import com.senkiv.bookstore.dto.UserResponseDto;
import com.senkiv.bookstore.exception.RegistrationException;
import com.senkiv.bookstore.mapper.UserMapper;
import com.senkiv.bookstore.model.User;
import com.senkiv.bookstore.repository.UserRepository;
import com.senkiv.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_WITH_SUCH_EMAIL_ALREADY_EXISTS =
            "User with such email already exists.";
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto dto) throws RegistrationException {
        User model = mapper.toModel(dto);
        if (!userRepository.existsByEmail(model.getEmail())) {
            return mapper.toDto(userRepository.save(model));
        } else {
            throw new RegistrationException(USER_WITH_SUCH_EMAIL_ALREADY_EXISTS);
        }
    }
}
