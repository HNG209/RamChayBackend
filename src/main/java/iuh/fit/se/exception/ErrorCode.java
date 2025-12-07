package iuh.fit.se.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED(9999, "[9999] Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_EXISTED(1001, "[1001] Username existed", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_FOUND(1002, "[1002] Username not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1003, "[1003] User not found", HttpStatus.NOT_FOUND),
    ROLE_EXISTED(2001, "[2001] Role existed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(2002, "[2002] Role not found", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND(2003, "[2003] Permission not found", HttpStatus.NOT_FOUND),
    FIELD_BLANK(2008, "[2008] Field cannot be blank", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(3000, "[3000] Invalid email", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(3001, "[3001] Invalid password", HttpStatus.UNAUTHORIZED),
    USERNAME_INVALID(3002, "[3002] Invalid username", HttpStatus.BAD_REQUEST),
    KEY_INVALID(3003, "[3003] Invalid enum key", HttpStatus.INTERNAL_SERVER_ERROR),
    OWNERSHIP_INVALID(3004, "[3004] Ownership invalid", HttpStatus.BAD_REQUEST),
    SESSION_EXPIRED(3005, "[3005] Session expired", HttpStatus.UNAUTHORIZED),
    CUSTOMER_NOT_ALLOWED(3006, "[3006] Customer not allowed", HttpStatus.FORBIDDEN),
    CUSTOMER_ONLY(3007, "[3007] Customer only", HttpStatus.FORBIDDEN),
    BODY_NOT_SPECIFIED(3008, "[3008] Body not specified", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(3009, "[3004] Invalid phone number", HttpStatus.BAD_REQUEST),
    PASSWORD_SIZE_INVALID(3010, "[3010] Password must be at least 6 digits", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(4004, "[4004] Unauthenticated", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_MISSING(4005, "[4005] Refresh token missing", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(4003, "[4003] Access denied", HttpStatus.FORBIDDEN),
//    INVALID_DOB(3003, "[3003] Invalid date of birth", HttpStatus.BAD_REQUEST),
    CUSTOMER_NOT_FOUND(4001, "[4001] Customer not found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(4002, "[4002] Product not found", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND(4003, "[4003] Cart item not found", HttpStatus.NOT_FOUND),
    CART_ITEM_INVALID(4004, "[4004] Cart item invalid", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(4005, "[4005] Cart not found", HttpStatus.NOT_FOUND),
    CART_EMPTY(4006, "[4006] Cart is empty", HttpStatus.BAD_REQUEST),
    PRODUCT_OUT_OF_STOCK(4007, "[4007] Product out of stock", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(4008, "[4008] Order not found", HttpStatus.NOT_FOUND),
    INVALID_QUANTITY(4009, "[4009] Invalid quantity", HttpStatus.BAD_REQUEST),
    ;


    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}