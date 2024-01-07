package ua.patronum.quicklink.restapi.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.patronum.quicklink.data.entity.User;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_USER_ID_LENGTH = 50;
    private static final int MIN_USER_ID_LENGTH = 3;
    private static final int MAX_PASSWORD_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String REGEX = "^(?=.*[A-Z])(?=.*\\d).";

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public RegistrationResponse register(RegistrationRequest request) {
        User existingUser = userService.findByUsername(request.getUsername());

        if (Objects.nonNull(existingUser)) {
            return RegistrationResponse.failed(RegistrationResponse.Error.USER_ALREADY_EXISTS);
        }

        Optional<RegistrationResponse.Error> validationError = validateRegistrationFields(request);

        if (validationError.isPresent()) {
            return RegistrationResponse.failed(validationError.get());
        }

        userService.saveUser(User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .role("ROLE_USER")
                .build());

        return RegistrationResponse.success();
    }

    public LoginResponse login(LoginRequest request) {
        Optional<LoginResponse.Error> validationError = validateLoginFields(request);

        if (validationError.isPresent()) {
            return LoginResponse.failed(validationError.get());
        }

        User user = userService.findByUsername(request.getUsername());

        if (Objects.isNull(user)) {
            return LoginResponse.failed(LoginResponse.Error.INVALID_USER_NAME);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return LoginResponse.failed(LoginResponse.Error.INVALID_PASSWORD);
        }

        String authToken = jwtUtil.generateToken(request.getUsername());

        return LoginResponse.success(authToken);
    }

    private Optional<RegistrationResponse.Error> validateRegistrationFields(RegistrationRequest request) {
        if (Objects.isNull(request.getUsername())
                || request.getUsername().length() > MAX_USER_ID_LENGTH
                || request.getUsername().length() <= MIN_USER_ID_LENGTH) {
            return Optional.of(RegistrationResponse.Error.INVALID_USERNAME);
        }

        if (Objects.isNull(request.getPassword())
                || (!isValidPassword(request.getPassword()))) {
            return Optional.of(RegistrationResponse.Error.INVALID_PASSWORD);
        }

        if (Objects.isNull(request.getConfirmPassword()) || !request.getPassword().equals(request.getConfirmPassword())) {
            return Optional.of(RegistrationResponse.Error.INVALID_CONFIRM_PASSWORD);
        }

        return Optional.empty();
    }

    private Optional<LoginResponse.Error> validateLoginFields(LoginRequest request) {
        if (Objects.isNull(request.getUsername())
                || request.getUsername().length() > MAX_USER_ID_LENGTH
                || request.getUsername().length() <= MIN_USER_ID_LENGTH) {
            return Optional.of(LoginResponse.Error.NAME_IS_EMPTY);
        }

        if (Objects.isNull(request.getPassword())
                || request.getPassword().length() > MAX_PASSWORD_LENGTH) {
            return Optional.of(LoginResponse.Error.INVALID_MAX_PASSWORD);
        }

        return Optional.empty();
    }

    private boolean isValidPassword(String password) {
        return Pattern
                .compile(REGEX + "{" + MIN_PASSWORD_LENGTH + "," + MAX_PASSWORD_LENGTH + "}$")
                .matcher(password).matches();
    }
}