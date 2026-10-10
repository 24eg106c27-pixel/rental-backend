package com.rental.config;

import com.rental.exception.ApiException;
import com.rental.model.Role;
import com.rental.model.User;
import com.rental.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// runs before every protected /api request: checks the token and the role for the URL
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokens;
    private final UserRepository users;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        if ("OPTIONS".equals(req.getMethod())) {
            return true;
        }

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ApiException(401, "Please login first");
        }
        Long userId = tokens.parse(header.substring(7));
        if (userId == null) {
            throw new ApiException(401, "Session expired, please login again");
        }
        User user = users.findById(userId).orElseThrow(() -> new ApiException(401, "User not found"));

        String path = req.getRequestURI();
        if (path.startsWith("/api/admin") && user.getRole() != Role.ADMIN) {
            throw new ApiException(403, "Admin access only");
        }
        if (path.startsWith("/api/owner") && user.getRole() != Role.OWNER) {
            throw new ApiException(403, "Property owner access only");
        }
        if (path.startsWith("/api/tenant") && user.getRole() != Role.TENANT) {
            throw new ApiException(403, "Tenant access only");
        }

        req.setAttribute("user", user);
        return true;
    }
}
