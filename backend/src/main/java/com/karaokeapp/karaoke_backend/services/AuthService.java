package com.karaokeapp.karaoke_backend.services;
import com.karaokeapp.karaoke_backend.security.JwtTokenProvider;
import com.karaokeapp.karaoke_backend.dto.LoginRequest;
import com.karaokeapp.karaoke_backend.dto.RegisterRequest;
import com.karaokeapp.karaoke_backend.models.Role;
import com.karaokeapp.karaoke_backend.models.User;
import com.karaokeapp.karaoke_backend.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;


//  @RequiredArgsConstructor z Lombok automatycznie wstrzykuje zależności przez konstruktor
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Zostanie dostarczony przez SecurityConfig
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;


    public void registerUser(RegisterRequest request) {
        // 1. Walidacja unikalności
        if (userRepository.existsByUsername(request.getUsername())) {
            // W profesjonalnym kodzie rzucasz tutaj wyjątek (np. UsernameAlreadyExistsException)
            throw new RuntimeException("Nazwa użytkownika jest już zajęta!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Adres e-mail jest już zajęty!");
        }

        // 2. Tworzenie obiektu User
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // 3. Szyfrowanie hasła PRZED zapisaniem w bazie!
        // Używamy passwordEncoder, który Spring Security zna.
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Nowi użytkownicy domyślnie otrzymują rolę USER
        user.setRole(Role.ROLE_USER);

        // 4. Zapis do bazy
        userRepository.save(user);
    }

    public String loginUser(LoginRequest request) {
        // 1. Próba uwierzytelnienia za pomocą AuthenticationManagera
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        return token;

    }


}