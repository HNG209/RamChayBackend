package iuh.fit.se.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import iuh.fit.se.dtos.request.CustomerRegistrationRequest;
import iuh.fit.se.dtos.request.LoginRequest;
import iuh.fit.se.dtos.response.CustomerRegistrationResponse;
import iuh.fit.se.dtos.response.LoginResponse;
import iuh.fit.se.dtos.response.MyProfileResponse;
import iuh.fit.se.entities.Customer;

import java.text.ParseException;
import java.util.Optional;

public interface AuthService {
    CustomerRegistrationResponse register(CustomerRegistrationRequest request);
    LoginResponse login(LoginRequest request, Long cartId) throws JOSEException;
    LoginResponse adminLogin(LoginRequest request) throws JOSEException;
    SignedJWT verify(String token) throws JOSEException, ParseException;
    MyProfileResponse getMyProfile(Long id);
    LoginResponse refreshToken(String token) throws JOSEException, ParseException;
}
