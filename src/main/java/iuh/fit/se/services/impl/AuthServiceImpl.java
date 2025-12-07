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
import iuh.fit.se.dtos.response.MyProfileResponse;
import iuh.fit.se.entities.Cart;
import iuh.fit.se.entities.Customer;
import iuh.fit.se.entities.Role;
import iuh.fit.se.entities.User;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.mappers.CustomerMapper;
import iuh.fit.se.repositories.CartRepository;
import iuh.fit.se.repositories.CustomerRepository;
import iuh.fit.se.repositories.UserRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CustomerMapper customerMapper;
    private final RoleService roleService;
    private final String SECRET_KEY;

    public AuthServiceImpl(JwtSecretReader reader,
                           CustomerRepository customerRepository,
                           CartRepository cartRepository, CustomerMapper customerMapper,
                           RoleService roleService,
                           UserService userService,
                           UserRepository userRepository) {
        SECRET_KEY = reader.getSecret();
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
        this.customerMapper = customerMapper;
        this.roleService = roleService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public CustomerRegistrationResponse register(CustomerRegistrationRequest request) {
        Customer customer = new Customer();

        if(customerRepository.findByUsername(request.getUsername()).isPresent())
            throw new AppException(ErrorCode.USERNAME_EXISTED);

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
    public LoginResponse login(LoginRequest request, Long cartId) throws JOSEException {
        User user = userService.findByUsername(request.getUsername());

        if (user == null)
            throw new AppException(ErrorCode.CUSTOMER_NOT_FOUND);

        user.getRoles().forEach(role -> {
            if(!role.getName().contains("ROLE_CUSTOMER"))
                throw new AppException(ErrorCode.CUSTOMER_ONLY);
        });

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }

        String refreshToken = generateToken(user, 7, ChronoUnit.DAYS);
        String accessToken = generateToken(user, 1, ChronoUnit.HOURS);

        if (cartId != null && user instanceof Customer customer) {
            // Tìm cart của người dùng đang đăng nhập
            Optional<Cart> existingCart = cartRepository.findCartByCustomerId(customer.getId());

            if (existingCart.isEmpty()) { // Nếu chưa có cart thì lấy cart trong cookies gán cho
                Optional<Cart> cartInCookies = cartRepository.findById(cartId);
                cartInCookies.ifPresent(cart -> { // Nếu cart trong cookies có tồn tại trong db
                    cart.setCustomer(customer); // Gán cho người dùng đó khi đăng nhập thành công
                    cartRepository.save(cart);
                });
            }
        }

        return LoginResponse.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .message("Đăng nhập thành công")
                .build();
    }

    @Override
    public LoginResponse adminLogin(LoginRequest request) throws JOSEException {
        User user = userService.findByUsername(request.getUsername());

        if (user == null)
            throw new AppException(ErrorCode.USER_NOT_FOUND);

        user.getRoles().forEach(role -> {
            if(role.getName().contains("ROLE_CUSTOMER"))
                throw new AppException(ErrorCode.CUSTOMER_NOT_ALLOWED);
        });

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }

        String refreshToken = generateToken(user, 7, ChronoUnit.DAYS);
        String accessToken = generateToken(user, 1, ChronoUnit.HOURS);

        return LoginResponse.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .message("Đăng nhập thành công vào tài khoản quản trị")
                .build();
    }

    private String generateToken(User user, long amount, ChronoUnit unit) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        // Thu thập permissions
        Set<String> permissions = new HashSet<>();
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(p -> permissions.add(p.getName()));
                }
            }
        }

        // List roles
        List<String> roles = user.getRoles() != null
                ? user.getRoles().stream().map(Role::getName).toList()
                : new ArrayList<>();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString()) // Subject là User ID
                .issuer("RamChay")
                .issueTime(new Date())
                .claim("permissions", permissions)
                .claim("roles", roles)
                .jwtID(UUID.randomUUID().toString())
                .expirationTime(new Date(
                        Instant.now().plus(amount, unit).toEpochMilli()
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

    @Override
    public LoginResponse refreshToken(String token) throws JOSEException, ParseException {
        // 1. Kiểm tra hiệu lực của Refresh Token gửi lên
        SignedJWT signedJWT = verify(token);

        // 2. Lấy User ID từ token (Subject)
        String userId = signedJWT.getJWTClaimsSet().getSubject();

        // 3. Kiểm tra xem User này còn tồn tại trong DB không (đề phòng user bị xóa)
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 4. Tạo cặp Token mới (Token Rotation)
        String newAccessToken = generateToken(user, 1, ChronoUnit.HOURS);
        String newRefreshToken = generateToken(user, 7, ChronoUnit.DAYS);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public MyProfileResponse getMyProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Set<String> permissions = new HashSet<>();
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(p -> permissions.add(p.getName()));
                }
            }
        }

        MyProfileResponse response = MyProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .permissions(permissions)
                .build();

        if (user instanceof Customer customer) {
            response.setPhones(customer.getPhones());
            response.setAddresses(customer.getAddresses());
        }

        return response;
    }
}
