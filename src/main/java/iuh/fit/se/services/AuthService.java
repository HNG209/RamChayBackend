package iuh.fit.se.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import iuh.fit.se.dtos.request.CustomerRegistrationRequest;
import iuh.fit.se.dtos.request.LoginRequest;
import iuh.fit.se.dtos.response.CustomerRegistrationResponse;
import iuh.fit.se.dtos.response.LoginResponse;

import java.text.ParseException;

public interface AuthService {
    CustomerRegistrationResponse register(CustomerRegistrationRequest request);
    LoginResponse login(LoginRequest request) throws JOSEException;
    SignedJWT verify(String token) throws JOSEException, ParseException;
}
