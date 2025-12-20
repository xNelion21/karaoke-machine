package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.UserResponseDTO;
import com.karaokeapp.karaoke_backend.dto.UserUpdateDTO;
import com.karaokeapp.karaoke_backend.models.Song;
import com.karaokeapp.karaoke_backend.models.User;
import com.karaokeapp.karaoke_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;
import com.karaokeapp.karaoke_backend.repositories.SongRepository;

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
    private final SongRepository songRepository;


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

    public UserResponseDTO updateProfile(Long userId, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono użytkownika o ID: " + userId));

        user = userMapper.updateEntityFromDTO(user, updateDTO);

        User updatedUser = userRepository.save(user);

        return userMapper.toResponseDTO(updatedUser);
    }

    public UserResponseDTO lockUser(Long userId, boolean lockStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono użytkownika o ID: " + userId));
        user.setLocked(lockStatus);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }

    //polubione
    public UserResponseDTO getUserDtoByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        if (user.getLikedSongs() != null) {
            Set<Long> ids = user.getLikedSongs().stream()
                    .map(Song::getId)
                    .collect(Collectors.toSet());
            dto.setLikedSongIds(ids);
        }
        return dto;
    }

    public void addLikedSong(String username, Long songId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony."));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Piosenka nie znaleziono."));

        user.getLikedSongs().add(song);
        userRepository.save(user);
    }

    public void removeLikedSong(String username, Long songId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje."));
        Song songToRemove = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Piosenka nie istnieje."));

        user.getLikedSongs().remove(songToRemove);
        userRepository.save(user);
    }
    
}