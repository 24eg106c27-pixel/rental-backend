package com.rental.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Creates and checks signed login tokens: base64(userId:expiry).signature
 * Nothing is stored on the server, so logins survive a server restart.
 */
@Component
public class TokenService {

    @Value("${app.jwt-secret}")
    private String secret;

    @Value("${app.token-hours}")
    private long hours;

    public String create(Long userId) {
        long expiry = System.currentTimeMillis() + hours * 3600_000L;
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString((userId + ":" + expiry).getBytes(StandardCharsets.UTF_8));
        return payload + "." + sign(payload);
    }

    // returns the user id, or null when the token is wrong or expired
    public Long parse(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 2) {
                return null;
            }
            boolean same = MessageDigest.isEqual(sign(parts[0]).getBytes(StandardCharsets.UTF_8),
                    parts[1].getBytes(StandardCharsets.UTF_8));
            if (!same) {
                return null;
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            String[] fields = payload.split(":");
            if (Long.parseLong(fields[1]) < System.currentTimeMillis()) {
                return null;
            }
            return Long.parseLong(fields[0]);
        } catch (Exception e) {
            return null;
        }
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Could not sign token", e);
        }
    }
}
