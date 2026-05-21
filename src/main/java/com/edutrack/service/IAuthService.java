package com.edutrack.service;

import com.edutrack.dto.request.LoginRequestDTO;
import com.edutrack.dto.response.LoginResponseDTO;

public interface IAuthService {
    LoginResponseDTO login(LoginRequestDTO dto);
}
