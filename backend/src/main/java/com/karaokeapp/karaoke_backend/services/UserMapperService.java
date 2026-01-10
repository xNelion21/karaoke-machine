package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.UserResponseDTO;
import com.karaokeapp.karaoke_backend.dto.UserUpdateDTO;
import com.karaokeapp.karaoke_backend.models.User; // Twoja encja User
import org.springframework.stereotype.Service;

@Service
public class UserMapperService {
    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setLocked(user.isLocked());

        //do statystyk
        if (user.getLikedSongs() != null) {
            dto.setLikedSongIds(user.getLikedSongs().stream()
                    .map(song -> song.getId())
                    .collect(java.util.stream.Collectors.toSet()));
        }

        return dto;
    }

    public User updateEntityFromDTO(User user, UserUpdateDTO dto) {
        if (user == null || dto == null) {
            return user;
        }

        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }


        return user;
    }

}