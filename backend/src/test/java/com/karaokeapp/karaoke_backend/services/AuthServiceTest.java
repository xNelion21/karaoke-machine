package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.FacebookLoginRequest;
import com.karaokeapp.karaoke_backend.dto.LoginRequest;
import com.karaokeapp.karaoke_backend.dto.RegisterRequest;
import com.karaokeapp.karaoke_backend.models.Role;
import com.karaokeapp.karaoke_backend.models.User;
import com.karaokeapp.karaoke_backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private AuthService authService;


    @Test
    void shouldRegisterUser_WhenDataIsUnique() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("nowyUser");
        request.setEmail("email@test.com");
        request.setPassword("tajneHaslo");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("tajneHaslo")).thenReturn("zakodowane_tajneHaslo");

        authService.registerUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("nowyUser", savedUser.getUsername());
        assertEquals("email@test.com", savedUser.getEmail());
        assertEquals("zakodowane_tajneHaslo", savedUser.getPassword());
        assertEquals(Role.ROLE_USER, savedUser.getRole());
    }

    @Test
    void shouldThrowException_WhenUsernameIsTaken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("zajetyLogin");

        when(userRepository.existsByUsername("zajetyLogin")).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.registerUser(request);
        });

        assertEquals("Nazwa użytkownika jest już zajęta!", exception.getMessage());
        verify(userRepository, never()).save(any());
    }


    @Test
    void shouldLoginUser_AndReturnJwtToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("jan");
        request.setPassword("haslo123");

        User user = new User();
        user.setUsername("jan");
        user.setLocked(false);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findByUsername("jan")).thenReturn(Optional.of(user));

        when(authentication.getPrincipal()).thenReturn(user);

        when(jwtService.generateToken(user)).thenReturn("fake.jwt.token");

        String token = authService.loginUser(request);

        assertEquals("fake.jwt.token", token);
    }

    @Test
    void shouldThrowException_WhenAccountIsLocked() {
        LoginRequest request = new LoginRequest();
        request.setUsername("zablokowany");
        request.setPassword("haslo");

        User lockedUser = new User();
        lockedUser.setUsername("zablokowany");
        lockedUser.setLocked(true);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByUsername("zablokowany")).thenReturn(Optional.of(lockedUser));

        assertThrows(LockedException.class, () -> {
            authService.loginUser(request);
        });
    }


    @Test
    void shouldCreateNewUser_WhenFacebookLoginIsFirstTime() {
        FacebookLoginRequest fbRequest = new FacebookLoginRequest();
        fbRequest.setEmail("fb@nowy.com");
        fbRequest.setName("Jan FB");

        when(userRepository.findByEmail("fb@nowy.com")).thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString())).thenReturn("hashed_random_pass");

        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        when(jwtService.generateToken(any(User.class))).thenReturn("fb_token");

        String token = authService.loginOrRegisterViaFacebook(fbRequest);

        assertEquals("fb_token", token);

        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("fb@nowy.com") &&
                        user.getUsername().equals("Jan FB") &&
                        user.getRole() == Role.ROLE_USER
        ));
    }
}