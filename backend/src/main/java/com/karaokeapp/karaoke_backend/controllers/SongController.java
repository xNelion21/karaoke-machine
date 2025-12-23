package com.karaokeapp.karaoke_backend.controllers;

import com.karaokeapp.karaoke_backend.dto.*;
import com.karaokeapp.karaoke_backend.services.SongService;
import com.karaokeapp.karaoke_backend.services.YoutubeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/{id}")
    public ResponseEntity<SongDetailsDTO> getSongById(@PathVariable Long id) {
        SongDetailsDTO song = songService.getSongById(id);
        return ResponseEntity.ok(song);
    }

    @PostMapping
    public ResponseEntity<SongResponseDTO> createSong(@Valid @RequestBody SongRequestDTO songRequestDTO) {
        // @Valid uruchamia walidację zdefiniowaną w SongRequestDTO
        SongResponseDTO newSong = songService.createSong(songRequestDTO);
        // Zwracamy 201 Created
        return new ResponseEntity<>(newSong, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<SongResponseDTO> updateSong(
            @PathVariable Long id,
            @Valid @RequestBody SongRequestDTO songRequestDTO) {


        SongResponseDTO updatedSong = songService.updateSong(id, songRequestDTO);
        return ResponseEntity.ok(updatedSong);
    }

    // 5. USUWANIE PIOSENKI (DELETE /api/songs/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {

        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<SongResponseDTO>> searchSongs(
            @RequestParam String query,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String genre
    ) {
        List<SongResponseDTO> songs = songService.searchSongs(query, artist, genre);
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<SongResponseDTO>> getSongsByCategory(@PathVariable Long categoryId) {
        List<SongResponseDTO> songs = songService.getSongsByCategory(categoryId);
        return ResponseEntity.ok(songs);
    }

    @PostMapping("/suggest")
    public ResponseEntity<Void> submitSuggestion(@Valid @RequestBody SuggestionRequestDTO suggestionDTO) {
        songService.submitSuggestion(suggestionDTO);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/search-online")
    public ResponseEntity<List<YoutubeSongDto>> searchOnline(@RequestParam String query) {
        List<YoutubeSongDto> results = youtubeService.searchSongs(query);
        return ResponseEntity.ok(results);
    }
}