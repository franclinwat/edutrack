package com.edutrack.controller;

import com.edutrack.dto.request.LoginRequestDTO;
import com.edutrack.dto.response.LoginResponseDTO;
import com.edutrack.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final IAuthService authService;

    // POST /api/auth/login — route publique (configurée dans SecurityConfig)
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        log.info("POST /api/auth/login");
        return ResponseEntity.ok(authService.login(dto));
    }
}
