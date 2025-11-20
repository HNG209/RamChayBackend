package iuh.fit.se.configs;

import com.nimbusds.jose.JOSEException;
import iuh.fit.se.services.AuthService;
import iuh.fit.se.utils.JwtSecretReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    private final AuthService authService;
    private final NimbusJwtDecoder nimbusJwtDecoder;

    @Autowired
    public CustomJwtDecoder(JwtSecretReader reader, AuthService authService) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(reader.getSecret().getBytes(), "HS256");
        nimbusJwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        this.authService = authService;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            authService.verify(token);
        } catch (JOSEException | ParseException e) {
            throw new RuntimeException(e);
        }

        return nimbusJwtDecoder.decode(token);
    }
}
