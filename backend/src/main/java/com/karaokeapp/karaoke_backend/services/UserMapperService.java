package com.karaokeapp.karaoke_backend.services; // Lub .mappers

import com.karaokeapp.karaoke_backend.dto.UserResponseDTO;
import com.karaokeapp.karaoke_backend.dto.UserUpdateDTO;
import com.karaokeapp.karaoke_backend.models.User; // Twoja encja User
import org.springframework.stereotype.Service;

@Service
public class UserMapperService {

    /**
     * Konwertuje encję User na DTO do wysłania do klienta.
     * Zapobiega wyciekowi wrażliwych danych (np. hasła).
     */
    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        return dto;
    }

    /**
     * Aktualizuje pola encji User na podstawie DTO odebranego od klienta.
     * Wykorzystywane przy zmianie danych profilowych.
     */
    public User updateEntityFromDTO(User user, UserUpdateDTO dto) {
        if (user == null || dto == null) {
            return user;
        }

        // Aktualizujemy tylko, jeśli pole w DTO nie jest puste
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        // UWAGA: Hasło nigdy nie jest aktualizowane w ten sposób
        // (Wymaga osobnej walidacji i serwisu)

        return user;
    }

    // Można tu dodać metodę toEntity(UserUpdateDTO), jeśli chcemy stworzyć nową encję
}