package com.senkiv.bookstore.service;

import com.senkiv.bookstore.dto.UserLoginRequestDto;
import com.senkiv.bookstore.dto.UserLoginResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto authenticate(UserLoginRequestDto requestDto);
}
