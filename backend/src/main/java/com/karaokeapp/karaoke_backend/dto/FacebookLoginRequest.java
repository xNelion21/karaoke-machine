package com.karaokeapp.karaoke_backend.dto;
import lombok.Data;

@Data
public class FacebookLoginRequest {
    private String facebookId;
    private String email;
    private String name;
    private String pictureUrl;
}