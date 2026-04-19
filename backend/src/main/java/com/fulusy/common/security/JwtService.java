package com.fulusy.common.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "JWT_SIGN_KEY", defaultValue = "change-me-to-a-long-random-string-at-least-32-chars-long")
    String signKey;

    public String issueToken(Long userId, String email) {
        try {
            String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
            long now = Instant.now().getEpochSecond();
            long exp = now + 86400; // 24 hours
            String payload = base64Url(
                "{\"sub\":\"" + userId + "\"," +
                "\"upn\":\"" + email + "\"," +
                "\"iss\":\"https://fulusy.app\"," +
                "\"groups\":[\"user\"]," +
                "\"userId\":" + userId + "," +
                "\"iat\":" + now + "," +
                "\"exp\":" + exp + "}"
            );
            String content = header + "." + payload;
            String signature = hmacSha256(content, signKey);
            return content + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JWT", e);
        }
    }

    private String base64Url(String input) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    private String hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
}
