package iuh.fit.se.services.impl;

import iuh.fit.se.entities.User;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.repositories.UserRepository;
import iuh.fit.se.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }
}
