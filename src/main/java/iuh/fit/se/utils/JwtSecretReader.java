package iuh.fit.se.utils;

import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtSecretReader {
    private final String secret;

    public JwtSecretReader(Environment env) {
        this.secret = env.getProperty("JWT_SECRET");
        if (this.secret == null) {
            throw new RuntimeException("Missing env var JWT_SECRET");
        }
    }
}
