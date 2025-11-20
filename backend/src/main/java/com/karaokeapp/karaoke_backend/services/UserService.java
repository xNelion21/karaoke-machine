package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.UserResponseDTO;
import com.karaokeapp.karaoke_backend.dto.UserUpdateDTO;
import com.karaokeapp.karaoke_backend.models.User;
import com.karaokeapp.karaoke_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;

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


    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDTO) // Konwersja każdej encji na DTO
                .collect(Collectors.toList());
    }

    public UserResponseDTO getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony w bazie."));

        return userMapper.toResponseDTO(user);
    }

    public UserResponseDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono użytkownika o ID: " + userId));
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

    public UserResponseDTO lockUser(Long userId, boolean lockStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono użytkownika o ID: " + userId));
        user.setLocked(lockStatus);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }

    // (Tu byłaby też metoda do zmiany hasła, ale wymagałaby PasswordEncoder)
}