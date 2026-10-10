package com.rental.controller;

import com.rental.dto.Dtos.*;
import com.rental.model.User;
import com.rental.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;

    @PostMapping("/api/auth/register")
    public AuthResponse register(@RequestBody RegisterRequest r) {
        return auth.register(r);
    }

    @PostMapping("/api/auth/login")
    public AuthResponse login(@RequestBody LoginRequest r) {
        return auth.login(r);
    }

    // current user (used by the frontend to refresh the verified flag)
    @GetMapping("/api/account/me")
    public User me(@RequestAttribute("user") User user) {
        return user;
    }
}
