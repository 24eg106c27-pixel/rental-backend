package com.rental.service;

import com.rental.config.TokenService;
import com.rental.dto.Dtos.*;
import com.rental.exception.ApiException;
import com.rental.exception.Check;
import com.rental.model.Role;
import com.rental.model.User;
import com.rental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final TokenService tokens;

    public AuthResponse register(RegisterRequest r) {
        Check.notBlank(r.name(), "Name is required");
        Check.notBlank(r.email(), "Email is required");
        Check.notBlank(r.password(), "Password is required");
        Check.that(r.email().contains("@"), 400, "Enter a valid email address");
        Check.that(r.password().length() >= 6, 400, "Password must be at least 6 characters");

        Role role;
        if ("OWNER".equalsIgnoreCase(r.role())) {
            role = Role.OWNER;
        } else if ("TENANT".equalsIgnoreCase(r.role())) {
            role = Role.TENANT;
        } else {
            throw new ApiException(400, "Choose Property Owner or Tenant");
        }

        String email = r.email().trim().toLowerCase();
        Check.that(!users.existsByEmail(email), 409, "This email is already registered");

        User u = new User();
        u.setName(r.name().trim());
        u.setEmail(email);
        u.setPassword(encoder.encode(r.password()));   // passwords are never stored as plain text
        u.setPhone(r.phone());
        u.setRole(role);
        u.setVerified(role == Role.TENANT);            // owners wait for admin verification
        users.save(u);
        return new AuthResponse(tokens.create(u.getId()), u);
    }

    public AuthResponse login(LoginRequest r) {
        Check.that(r.email() != null && r.password() != null, 400, "Enter email and password");
        User u = users.findByEmail(r.email().trim().toLowerCase())
                .orElseThrow(() -> new ApiException(401, "Invalid email or password"));
        Check.that(encoder.matches(r.password(), u.getPassword()), 401, "Invalid email or password");
        return new AuthResponse(tokens.create(u.getId()), u);
    }
}
