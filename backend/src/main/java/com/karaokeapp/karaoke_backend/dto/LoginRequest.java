package com.karaokeapp.karaoke_backend.dto;

import lombok.Data;

@Data
public class LoginRequest {
    // Użytkownik loguje się za pomocą nazwy użytkownika i hasła
    private String username;
    private String password;
}