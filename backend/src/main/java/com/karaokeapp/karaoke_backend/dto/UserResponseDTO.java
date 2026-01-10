package com.karaokeapp.karaoke_backend.dto;
import com.karaokeapp.karaoke_backend.models.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private Set<Long> likedSongIds;
    private boolean locked;
}