package com.karaokeapp.karaoke_backend.controllers;

import com.karaokeapp.karaoke_backend.dto.*;
import com.karaokeapp.karaoke_backend.models.Song;
import com.karaokeapp.karaoke_backend.services.SongService;
import com.karaokeapp.karaoke_backend.services.YoutubeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final YoutubeService youtubeService;

    @GetMapping
    public ResponseEntity<List<SongResponseDTO>> getAllSongs() {
        List<SongResponseDTO> songs = songService.getAllSongs();
        return ResponseEntity.ok(songs);
    }

    // 2. POBIERANIE SZCZEGÓŁÓW PIOSENKI (GET /api/songs/{id})
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<SongDetailsDTO> getSongById(@PathVariable Long id) {
        SongDetailsDTO song = songService.getSongById(id);
        return ResponseEntity.ok(song);
    }

    // RĘCZNE DODAWANIE PIOSENKI
    @PostMapping
    public ResponseEntity<SongResponseDTO> createSong(
            @Valid @RequestBody SongRequestDTO songRequestDTO
    ) {
        SongResponseDTO newSong = songService.createSong(songRequestDTO);
        return new ResponseEntity<>(newSong, HttpStatus.CREATED);
    }

    // IMPORT Z YOUTUBE
    @PostMapping("/import")
    public ResponseEntity<SongResponseDTO> importFromYoutube(
            @Valid @RequestBody YoutubeSongDto youtubeSongDto
    ) {
        SongResponseDTO newSong = songService.importFromYoutube(youtubeSongDto);
        return new ResponseEntity<>(newSong, HttpStatus.CREATED);
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<SongResponseDTO> updateSong(
            @PathVariable Long id,
            @Valid @RequestBody SongRequestDTO songRequestDTO
    ) {
        SongResponseDTO updatedSong = songService.updateSong(id, songRequestDTO);
        return ResponseEntity.ok(updatedSong);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<SongResponseDTO>> searchSongs(
            @RequestParam String query,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String category
    ) {
        List<SongResponseDTO> songs = songService.searchSongs(query, artist, category);
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<SongResponseDTO>> getSongsByCategory(
            @PathVariable Long categoryId
    ) {
        List<SongResponseDTO> songs = songService.getSongsByCategory(categoryId);
        return ResponseEntity.ok(songs);
    }

    @PostMapping("/suggest")
    public ResponseEntity<Void> submitSuggestion(
            @Valid @RequestBody SuggestionRequestDTO suggestionDTO
    ) {
        songService.submitSuggestion(suggestionDTO);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/search-online")
    public ResponseEntity<List<YoutubeSongDto>> searchOnline(
            @RequestParam String query
    ) {
        List<YoutubeSongDto> results = youtubeService.searchSongs(query);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/like")
    public ResponseEntity<Song> likeSong(
            @RequestBody YoutubeSongDto youtubeSongDto
    ) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Song savedSong = songService.likeYoutubeSong(youtubeSongDto, username);
        return ResponseEntity.ok(savedSong);
    }

    @GetMapping("/my-liked-songs")
    public ResponseEntity<Set<Song>> getMyLikedSongs() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Set<Song> mySongs = songService.getSongsForUser(username);
        return ResponseEntity.ok(mySongs);
    }
}
