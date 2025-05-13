package com.senkiv.bookstore.service;

import com.senkiv.bookstore.dto.UserRegistrationRequestDto;
import com.senkiv.bookstore.dto.UserResponseDto;
import com.senkiv.bookstore.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto dto) throws RegistrationException;
}
