package ua.patronum.quicklink.restapi.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.patronum.quicklink.data.entity.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_Registration_Success() {

        RegistrationRequest request = new RegistrationRequest("testUser",
                             "Password123", "Password123");
        when(userService.findByUsername("testUser")).thenReturn(null);
        doNothing().when(userService).saveUser(any(User.class));

        RegistrationResponse response = authService.register(request);
        assertEquals(RegistrationResponse.Error.OK, response.getError());

        verify(userService, times(1)).findByUsername("testUser");
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void register_UserAlreadyExists_Failed() {

        RegistrationRequest request = new RegistrationRequest("existingUser",
                                 "Password123", "Password123");
        User existingUser = User.builder().username("existingUser").build();
        when(userService.findByUsername("existingUser")).thenReturn(existingUser);

        RegistrationResponse response = authService.register(request);
        assertEquals(RegistrationResponse.Error.USER_ALREADY_EXISTS, response.getError());

        verify(userService, times(1)).findByUsername("existingUser");
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void register_Invalid_Username_Failed() {

        RegistrationRequest request = new RegistrationRequest("tt", "test", "test");
        when(userService.findByUsername("tt")).thenReturn(null);

        RegistrationResponse response = authService.register(request);
        assertEquals(RegistrationResponse.Error.INVALID_USERNAME, response.getError());

        verify(userService, times(1)).findByUsername("tt");
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void register_Invalid_Password_Failed() {

        RegistrationRequest request = new RegistrationRequest("Test", "tt", "tt");
        when(userService.findByUsername("Test")).thenReturn(null);

        RegistrationResponse response = authService.register(request);
        assertEquals(RegistrationResponse.Error.INVALID_PASSWORD, response.getError());

        verify(userService, times(1)).findByUsername("Test");
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void register_Invalid_Confirm_Password_Failed() {

        RegistrationRequest request = new RegistrationRequest("Test",
                                 "Test1234", "Test12");
        when(userService.findByUsername("Test")).thenReturn(null);

        RegistrationResponse response = authService.register(request);
        assertEquals(RegistrationResponse.Error.INVALID_CONFIRM_PASSWORD, response.getError());

        verify(userService, times(1)).findByUsername("Test");
        verify(userService, never()).saveUser(any(User.class));
    }


    @Test
    void login_Success() {

        LoginRequest request = new LoginRequest("Test", "Test1234");
        User existingUser = User.builder().username("Test").password("Test1234").build();
        when(userService.findByUsername("Test")).thenReturn(existingUser);
        when(passwordEncoder.matches("Test1234", existingUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("Test")).thenReturn("dummyToken");

        LoginResponse response = authService.login(request);
        assertEquals(LoginResponse.Error.OK, response.getError());
        assertNotNull(response.getToken());

        verify(userService, times(1)).findByUsername("Test");
        verify(passwordEncoder, times(1))
                .matches("Test1234", existingUser.getPassword());
        verify(jwtUtil, times(1)).generateToken("Test");
    }

    @Test
    void login_Name_Is_Empty_Failed() {

        LoginRequest request = new LoginRequest("", "TestFailed");

        LoginResponse response = authService.login(request);
        assertEquals(LoginResponse.Error.NAME_IS_EMPTY, response.getError());
        assertNull(response.getToken());

        verify(userService, times(0)).findByUsername("");
        verify(jwtUtil, times(0)).generateToken("");
    }

    @Test
    void login_Invalid_Username_Failed() {

        LoginRequest request = new LoginRequest("Test1", "TestFailed");

        LoginResponse response = authService.login(request);
        assertEquals(LoginResponse.Error.INVALID_USER_NAME, response.getError());
        assertNull(response.getToken());

        verify(userService, times(1)).findByUsername("Test1");
        verify(jwtUtil, times(0)).generateToken("Test1");
    }

    @Test
    void login_Invalid_Password_Failed() {

        LoginRequest request = new LoginRequest("Test", "Test5678");
        User existingUser = User.builder().username("Test").password("Test5678").build();
        when(userService.findByUsername("Test")).thenReturn(existingUser);
        when(passwordEncoder.matches("Test5678", existingUser.getPassword())).thenReturn(false);

        LoginResponse response = authService.login(request);
        assertEquals(LoginResponse.Error.INVALID_PASSWORD, response.getError());
        assertNull(response.getToken());

        verify(userService, times(1)).findByUsername("Test");
        verify(passwordEncoder, times(1))
                .matches("Test5678", existingUser.getPassword());
        verify(jwtUtil, times(0)).generateToken("Test");
    }

    @Test
    void login_Invalid_Password_Max_length_Failed() {

        LoginRequest request = new LoginRequest("Test",
                "12345678-10-2345678-20-2345678-30-2345678-40-2345678-50-2");

        LoginResponse response = authService.login(request);
        assertEquals(LoginResponse.Error.INVALID_MAX_PASSWORD, response.getError());
        assertNull(response.getToken());

        verify(userService, times(0)).findByUsername("Test1");
        verify(jwtUtil, times(0)).generateToken("Test1");
    }
}