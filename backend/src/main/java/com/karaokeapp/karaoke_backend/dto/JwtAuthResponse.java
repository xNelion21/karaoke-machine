package com.karaokeapp.karaoke_backend.dto;

import lombok.Data;

@Data
public class JwtAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer"; // Standardowy typ tokena dla JWT

    public JwtAuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}