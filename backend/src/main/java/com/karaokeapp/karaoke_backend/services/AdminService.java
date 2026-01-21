package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.SuggestionResponseDTO;
import com.karaokeapp.karaoke_backend.models.*;
import com.karaokeapp.karaoke_backend.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final SuggestionRepository suggestionRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;


    public List<SuggestionResponseDTO> getPendingSuggestions() {
        return suggestionRepository.findByStatus(SuggestionStatus.PENDING)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private SuggestionResponseDTO mapToResponseDTO(Suggestion s) {
        Song song = s.getSong();
        return SuggestionResponseDTO.builder()

                .id(s.getId())
                .songId(s.getSong().getId())
                .songTitle(s.getSong().getTitle())
                .userId(s.getUser().getId())
                .username(s.getUser().getUsername())
                .currentLyrics(s.getSong().getLyrics())
                .currentCategories(song.getCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.toSet()))
                .proposedLyrics(s.getProposedLyrics())
                .proposedContent(s.getProposedContent())
                .status(s.getStatus())
                .createdAt(s.getCreatedAt())
                .build();
    }

    @Transactional
    public void processSuggestion(Long suggestionId, SuggestionStatus newStatus) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Propozycja o ID " + suggestionId + " nie istnieje."));

        suggestion.setStatus(newStatus);
        suggestionRepository.save(suggestion);

        if (newStatus == SuggestionStatus.ACCEPTED) {
            Song song = suggestion.getSong();

            if (suggestion.getProposedLyrics() != null) {
                song.setLyrics(suggestion.getProposedLyrics());
            }

            songRepository.save(song);

        }
    }

    @Transactional
    public void updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik o ID " + userId + " nie istnieje"));
        String formattedRole = newRole.toUpperCase();
        if (!formattedRole.startsWith("ROLE_")) {
            formattedRole = "ROLE_" + formattedRole;
        }
        try {
            user.setRole(Role.valueOf(formattedRole));
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nieprawidłowa rola. Dozwolone: ROLE_USER, ROLE_ADMIN");
        }
    }

}