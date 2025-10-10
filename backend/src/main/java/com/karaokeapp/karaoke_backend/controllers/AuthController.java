package com.karaokeapp.karaoke_backend.controllers;
import com.karaokeapp.karaoke_backend.dto.LoginRequest;
import com.karaokeapp.karaoke_backend.dto.RegisterRequest;
import com.karaokeapp.karaoke_backend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.karaokeapp.karaoke_backend.dto.JwtAuthResponse;

@RestController // Oznacza, że klasa jest kontrolerem REST i zwraca JSON/XML
@RequestMapping("/api/auth") // Bazowy URL dla wszystkich metod w tym kontrolerze
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Metoda POST do rejestracji nowego użytkownika
    // URL: POST http://localhost:8080/api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            // Przekazanie żądania do warstwy serwisowej
            authService.registerUser(registerRequest);

            // Jeśli rejestracja się powiedzie, zwróć status 201 Created z komunikatem
            return new ResponseEntity<>("Użytkownik zarejestrowany pomyślnie!", HttpStatus.CREATED);

        } catch (RuntimeException e) {
            // Jeśli wystąpi wyjątek (np. nazwa/email zajęty), zwróć status 400 Bad Request
            // Zwrócenie komunikatu o błędzie jako treść odpowiedzi
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Tutaj w przyszłości zaimplementujesz metodę logowania:
    // @PostMapping("/login")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {


            String token = authService.loginUser(loginRequest);
            return new ResponseEntity<>(new JwtAuthResponse(token), HttpStatus.OK);


    }

}