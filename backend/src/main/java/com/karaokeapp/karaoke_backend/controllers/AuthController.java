package com.karaokeapp.karaoke_backend.controllers;
import com.karaokeapp.karaoke_backend.dto.LoginRequest;
import com.karaokeapp.karaoke_backend.dto.RegisterRequest;
import com.karaokeapp.karaoke_backend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.karaokeapp.karaoke_backend.dto.JwtAuthResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.registerUser(registerRequest);
            return new ResponseEntity<>("Użytkownik zarejestrowany pomyślnie!", HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {


            String token = authService.loginUser(loginRequest);
            return new ResponseEntity<>(new JwtAuthResponse(token), HttpStatus.OK);


    }

}