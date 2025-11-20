package com.karaokeapp.karaoke_backend.services;
import com.karaokeapp.karaoke_backend.dto.SongDetailsDTO;
import com.karaokeapp.karaoke_backend.dto.SongRequestDTO;
import com.karaokeapp.karaoke_backend.dto.SongResponseDTO;
import com.karaokeapp.karaoke_backend.dto.SuggestionRequestDTO;
import com.karaokeapp.karaoke_backend.models.Song;
import com.karaokeapp.karaoke_backend.models.Suggestion;
import com.karaokeapp.karaoke_backend.models.User;
import com.karaokeapp.karaoke_backend.repositories.CategoryRepository;
import com.karaokeapp.karaoke_backend.repositories.SongRepository;
import com.karaokeapp.karaoke_backend.repositories.SuggestionRepository;
import com.karaokeapp.karaoke_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}



@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final SongMapperService songMapper;
    private final CategoryRepository categoryRepository;
    private final SuggestionRepository suggestionRepository;
    private final UserRepository userRepository;

    public SongResponseDTO createSong(SongRequestDTO requestDTO) {
        if (songRepository.existsByTitle(requestDTO.getTitle())) {
            throw new RuntimeException("Piosenka o tytule '" + requestDTO.getTitle() + "' już istnieje.");
        }

        Song songToSave = songMapper.toEntity(requestDTO);
        Song savedSong = songRepository.save(songToSave);

        return songMapper.toResponseDTO(savedSong);
    }

    public List<SongResponseDTO> getAllSongs() {
        return songRepository.findAll().stream()
                .map(songMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public SongDetailsDTO getSongById(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono piosenki o ID: " + id));
        return songMapper.toDetailsDTO(song);
    }


    public SongResponseDTO updateSong(Long id, SongRequestDTO requestDTO) {
        Song existingSong = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono piosenki o ID: " + id));

        Song songToUpdate = songMapper.updateEntityFromDTO(existingSong, requestDTO);
        Song updatedSong = songRepository.save(songToUpdate);
        return songMapper.toResponseDTO(updatedSong);
    }

    public void deleteSong(Long id) {
        if (!songRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie można usunąć: nie znaleziono piosenki o ID: " + id);
        }
        songRepository.deleteById(id);
    }

    public List<SongResponseDTO> searchSongs(String query, String artist, String genre) {
        if (query == null || query.isBlank()) {
            throw new BadRequestException("Parametr wyszukiwania nie może być pusty");
        }

        List<Song> songs;

        if (artist != null && !artist.isBlank() && genre != null && !genre.isBlank()) {
            songs = songRepository.findByTitleContainingIgnoreCaseAndAuthors_NameContainingIgnoreCaseAndGenreIgnoreCase(query,artist, genre);
        }
        else if (artist != null && !artist.isBlank()) {
            songs = songRepository.findByTitleContainingIgnoreCaseAndAuthors_NameContainingIgnoreCase(query, artist);
        }
        else if (genre != null && !genre.isBlank()) {
            songs = songRepository.findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCase(query, genre);
        }
        else {
            songs = songRepository.findByTitleContainingIgnoreCase(query);
        }

        if (songs.isEmpty()) {
            throw new ResourceNotFoundException("Nie znaleziono żadnych piosenek dla zapytania: " + query);
        }

        return songs.stream()
                .map(songMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<SongResponseDTO> getSongsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)){
            throw new ResourceNotFoundException("Nie znaleziono kategorii o id: " + categoryId);
        }

        List<Song> songs = songRepository.findByCategories_Id(categoryId);
        return songs.stream()
                .map(songMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void submitSuggestion(SuggestionRequestDTO dto) {

        Song song = songRepository.findById(dto.getSongId())
                .orElseThrow(() -> new ResourceNotFoundException("Piosenka o ID " + dto.getSongId() + " nie istnieje."));


        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Zalogowany użytkownik nie istnieje."));


        Suggestion suggestion = new Suggestion();
        suggestion.setSong(song);
        suggestion.setUser(user);
        suggestion.setProposedContent(dto.getProposedContent());

        suggestionRepository.save(suggestion);
    }

    }