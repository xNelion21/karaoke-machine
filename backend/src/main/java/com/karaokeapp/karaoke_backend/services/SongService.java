package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.*;
import com.karaokeapp.karaoke_backend.models.*;
import com.karaokeapp.karaoke_backend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;
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
    private final AuthorRepository authorRepository;

    public SongResponseDTO createSong(SongRequestDTO requestDTO) {
        if (requestDTO.getTitle() == null || requestDTO.getTitle().isBlank()) {
            throw new BadRequestException("Tytuł piosenki jest wymagany");
        }

        if (songRepository.existsByTitle(requestDTO.getTitle())) {
            throw new RuntimeException("Piosenka o tytule '" + requestDTO.getTitle() + "' już istnieje.");
        }

        Song songToSave = songMapper.toEntity(requestDTO);
        Song savedSong = songRepository.save(songToSave);
        return songMapper.toResponseDTO(savedSong);
    }

    public List<SongResponseDTO> getAllSongs() {
        return songRepository.findAll()
                .stream()
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

        Song updated = songMapper.updateEntityFromDTO(existingSong, requestDTO);
        return songMapper.toResponseDTO(songRepository.save(updated));
    }

    public void deleteSong(Long id) {
        if (!songRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie można usunąć: nie znaleziono piosenki o ID: " + id);
        }
        songRepository.deleteById(id);
    }

    public List<SongResponseDTO> searchSongs(String query, String artist, String category) {
        if (query == null || query.isBlank()) {
            throw new BadRequestException("Parametr wyszukiwania nie może być pusty");
        }

        List<Song> songs = (artist != null && !artist.isBlank())
                ? songRepository.findByTitleContainingIgnoreCaseAndAuthors_NameContainingIgnoreCase(query, artist)
                : songRepository.findByTitleContainingIgnoreCase(query);

        if (songs.isEmpty()) {
            throw new ResourceNotFoundException("Nie znaleziono żadnych piosenek");
        }

        return songs.stream()
                .map(songMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<SongResponseDTO> getSongsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Nie znaleziono kategorii o id: " + categoryId);
        }

        return songRepository.findByCategories_Id(categoryId)
                .stream()
                .map(songMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void submitSuggestion(SuggestionRequestDTO dto) {
        Song song;

        if ((dto.getProposedLyrics() == null || dto.getProposedLyrics().isBlank()) &&
                (dto.getProposedContent() == null || dto.getProposedContent().isBlank())) {
            throw new BadRequestException("Sugestia musi zawierać treść poprawki lub nowy tekst piosenki.");
        }

        if (dto.getSongId() != null) {
            song = songRepository.findById(dto.getSongId())
                    .orElseThrow(() -> new ResourceNotFoundException("Piosenka o ID " + dto.getSongId() + " nie istnieje."));
        } else if (dto.getYoutubeSongData() != null) {
            song = getOrCreateSong(dto.getYoutubeSongData());
        } else {
            throw new BadRequestException("Brak identyfikatora lub danych piosenki.");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje."));

        Suggestion suggestion = new Suggestion();
        suggestion.setSong(song);
        suggestion.setUser(user);
        suggestion.setProposedLyrics(dto.getProposedLyrics());
        suggestion.setProposedContent(dto.getProposedContent());
        suggestion.setStatus(SuggestionStatus.PENDING);

        suggestionRepository.save(suggestion);
    }

    public Song likeYoutubeSong(YoutubeSongDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        Song song = getOrCreateSong(dto);

        if (!user.getLikedSongs().contains(song)) {
            user.getLikedSongs().add(song);
            userRepository.save(user);
        }

        return song;
    }

    public SongResponseDTO importFromYoutube(YoutubeSongDto dto) {
        return songMapper.toResponseDTO(getOrCreateSong(dto));
    }

    public Song getOrCreateSong(YoutubeSongDto dto) {

        if (dto.getVideoId() == null || dto.getVideoId().isBlank()) {
            throw new BadRequestException("Brak videoId z YouTube");
        }

        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new BadRequestException("Brak tytułu piosenki z YouTube");
        }

        String fullYoutubeUrl = "https://www.youtube.com/watch?v=" + dto.getVideoId();

        Optional<Song> existing = songRepository.findByYoutubeUrl(fullYoutubeUrl);
        if (existing.isPresent()) {
            return existing.get();
        }

        Song song = new Song();
        song.setTitle(dto.getTitle());
        song.setYoutubeUrl(fullYoutubeUrl);
        song.setThumbnailUrl(dto.getThumbnailUrl());
        song.setLyrics(dto.getLyrics());

        Category category = categoryRepository.findById(3L)
                .orElseGet(() -> categoryRepository.findAll().stream().findFirst().orElse(null));

        if (category != null) {
            song.getCategories().add(category);
        }

        if (dto.getArtist() != null && !dto.getArtist().isBlank()) {
            Author author = authorRepository.findByName(dto.getArtist().trim())
                    .orElseGet(() -> authorRepository.save(
                            Author.builder().name(dto.getArtist().trim()).build()
                    ));
            song.getAuthors().add(author);
        }

        return songRepository.save(song);
    }

    public Set<Song> getSongsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));
        return user.getLikedSongs();
    }
}
