package iuh.fit.se.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

public interface AuthService {
    SignedJWT verify(String token) throws JOSEException, ParseException;
}
