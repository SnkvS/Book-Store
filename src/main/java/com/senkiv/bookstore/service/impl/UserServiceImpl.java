package com.senkiv.bookstore.service.impl;

import com.senkiv.bookstore.dto.UserRegistrationRequestDto;
import com.senkiv.bookstore.dto.UserResponseDto;
import com.senkiv.bookstore.exception.RegistrationException;
import com.senkiv.bookstore.mapper.UserMapper;
import com.senkiv.bookstore.model.Role.RoleName;
import com.senkiv.bookstore.model.User;
import com.senkiv.bookstore.repository.RoleRepository;
import com.senkiv.bookstore.repository.UserRepository;
import com.senkiv.bookstore.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_WITH_SUCH_EMAIL_ALREADY_EXISTS =
            "User with such email already exists -> %s";
    private static final String CANNOT_ASSIGN_DEFAULT_ROLE_TO_USER =
            "Cannot assign default role to user.";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;

    @Transactional
    @Override
    public UserResponseDto register(UserRegistrationRequestDto dto) throws RegistrationException {
        if (userRepository.existsByEmail(dto.email())) {
            throw new RegistrationException(
                    USER_WITH_SUCH_EMAIL_ALREADY_EXISTS.formatted(dto.email()));
        }
        User model = mapper.toModel(dto);
        if (!roleRepository.existsRoleByRoleName(RoleName.USER)) {
            throw new RegistrationException(
                    "Cannot assign role (%s) to user (%s)".formatted(RoleName.USER, model));
        }
        model.getRoles().add(roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> new RegistrationException(
                        CANNOT_ASSIGN_DEFAULT_ROLE_TO_USER)));
        return mapper.toDto(userRepository.save(model));
    }
}
