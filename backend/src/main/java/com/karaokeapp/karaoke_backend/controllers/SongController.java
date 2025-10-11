package com.karaokeapp.karaoke_backend.controllers;

import com.karaokeapp.karaoke_backend.dto.SongRequestDTO;
import com.karaokeapp.karaoke_backend.dto.SongResponseDTO;
import com.karaokeapp.karaoke_backend.services.SongService;
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

    // 1. POBIERANIE LISTY PIOSENEK (GET /api/songs)
    // Dostęp dla ROLE_USER / ROLE_ADMIN (zgodnie z SecurityConfig)
    @GetMapping
    public ResponseEntity<List<SongResponseDTO>> getAllSongs() {
        List<SongResponseDTO> songs = songService.getAllSongs();
        return ResponseEntity.ok(songs);
    }

    // 2. POBIERANIE SZCZEGÓŁÓW PIOSENKI (GET /api/songs/{id})
    @GetMapping("/{id}")
    public ResponseEntity<SongResponseDTO> getSongById(@PathVariable Long id) {
        SongResponseDTO song = songService.getSongById(id);
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
}