package com.karaokeapp.karaoke_backend.services;
import com.karaokeapp.karaoke_backend.dto.LoginRequest;
import com.karaokeapp.karaoke_backend.dto.RegisterRequest;
import com.karaokeapp.karaoke_backend.models.Role;
import com.karaokeapp.karaoke_backend.models.User;
import com.karaokeapp.karaoke_backend.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public void registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Nazwa użytkownika jest już zajęta!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Adres e-mail jest już zajęty!");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
    }

    public String loginUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtService.generateToken(userDetails);

    }

    public String loginOrRegisterViaFacebook(com.karaokeapp.karaoke_backend.dto.FacebookLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(request.getName());
                    newUser.setEmail(request.getEmail());
                    newUser.setPassword(passwordEncoder.encode("FB_USER_" + java.util.UUID.randomUUID()));
                    newUser.setRole(Role.ROLE_USER);
                    newUser.setLocked(false);
                    return userRepository.save(newUser);
                });

        if (user.isLocked()) {
            throw new RuntimeException("Twoje konto jest zablokowane!");
        }

        return jwtService.generateToken(user);
    }


}