package iuh.fit.se.services.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import iuh.fit.se.dtos.request.CustomerRegistrationRequest;
import iuh.fit.se.dtos.request.LoginRequest;
import iuh.fit.se.dtos.response.CustomerRegistrationResponse;
import iuh.fit.se.dtos.response.LoginResponse;
import iuh.fit.se.entities.Customer;
import iuh.fit.se.entities.Role;
import iuh.fit.se.entities.User;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.mappers.CustomerMapper;
import iuh.fit.se.repositories.CustomerRepository;
import iuh.fit.se.services.AuthService;
import iuh.fit.se.services.RoleService;
import iuh.fit.se.services.UserService;
import iuh.fit.se.utils.JwtSecretReader;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final CustomerMapper customerMapper;
    private final RoleService roleService;
    private final String SECRET_KEY;

    public AuthServiceImpl(JwtSecretReader reader,
                           CustomerRepository customerRepository,
                           CustomerMapper customerMapper,
                           RoleService roleService,
                           UserService userService) {
        SECRET_KEY = reader.getSecret();
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.roleService = roleService;
        this.userService = userService;
    }

    @Override
    public CustomerRegistrationResponse register(CustomerRegistrationRequest request) {
        Customer customer = new Customer();

        Role role = roleService.findByName("ROLE_CUSTOMER");

        // hash mật khẩu
        String hashed = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        customer.setUsername(request.getUsername());
        customer.setPassword(hashed);
        customer.setFullName(request.getFullName());
        customer.setPhones(Set.of(request.getPhone()));

        // Gán role là CUSTOMER khi đăng kí
        customer.setRoles(Set.of(role));

        return customerMapper.toCustomerRegistrationResponse(
                customerRepository.save(customer));
    }

    @Override
    public LoginResponse login(LoginRequest request) throws JOSEException {
        User user = userService.findByUsername(request.getUsername());

        user.getRoles().forEach(System.out::println);

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }

        String token = generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .message("Đăng nhập thành công")
                .build();
    }

    public String generateToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        Set<String> permissions = new HashSet<>();

        for (Role role : user.getRoles()) {
            role.getPermissions().forEach(p -> permissions.add(p.getName()));
        }

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("RamChay")
                .issueTime(new Date())
                .claim("permissions", permissions)
                .claim("roles", user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .toList())
                .jwtID(UUID.randomUUID().toString())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        jwsObject.sign(new MACSigner(SECRET_KEY.getBytes(StandardCharsets.UTF_8)));

        return jwsObject.serialize();
    }

    @Override
    public SignedJWT verify(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean expired = exp.before(new Date());

        if (expired)
            throw new CredentialsExpiredException("Session expired");

        boolean verified = signedJWT.verify(verifier);

        if (!verified)
            throw new BadCredentialsException("Bad token");

        return signedJWT;
    }
}
