package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.UserResponseDTO;
import com.karaokeapp.karaoke_backend.dto.UserUpdateDTO;
import com.karaokeapp.karaoke_backend.models.User;
import com.karaokeapp.karaoke_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;
class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapperService userMapper;

    // UŻYTECZNA METODA 1: Pobieranie profilu użytkownika (GET /api/users/{id})
    public UserResponseDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono użytkownika o ID: " + userId));

        // Konwersja bezpiecznej encji na DTO
        return userMapper.toResponseDTO(user);
    }

    // UŻYTECZNA METODA 2: Aktualizacja profilu użytkownika (PUT /api/users/{id})
    public UserResponseDTO updateProfile(Long userId, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono użytkownika o ID: " + userId));

        // Aktualizacja pól encji na podstawie DTO
        user = userMapper.updateEntityFromDTO(user, updateDTO);

        // Zapis zaktualizowanej encji do bazy danych
        User updatedUser = userRepository.save(user);

        // Zwrócenie zaktualizowanych danych jako DTO
        return userMapper.toResponseDTO(updatedUser);
    }

    // (Tu byłaby też metoda do zmiany hasła, ale wymagałaby PasswordEncoder)
}