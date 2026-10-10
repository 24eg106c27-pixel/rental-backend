package com.rental.dto;

import com.rental.model.User;

import java.time.LocalDate;

// request and response objects (records are small immutable classes)
public class Dtos {

    public record LoginRequest(String email, String password) {
    }

    public record RegisterRequest(String name, String email, String password, String phone, String role) {
    }

    public record AuthResponse(String token, User user) {
    }

    public record RequestForm(Long propertyId, String message, LocalDate moveInDate, Integer months) {
    }

    public record PayForm(String method) {
    }

    public record ComplaintForm(String subject, String description) {
    }

    public record ResolveForm(String response) {
    }
}
