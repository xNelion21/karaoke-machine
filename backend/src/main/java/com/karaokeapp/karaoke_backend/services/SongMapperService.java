package com.karaokeapp.karaoke_backend.services; // Lub .mappers

import com.karaokeapp.karaoke_backend.dto.SongRequestDTO;
import com.karaokeapp.karaoke_backend.dto.SongResponseDTO;
import com.karaokeapp.karaoke_backend.models.Song; // Encja Song (założenie, że Marcin ją stworzył)
import org.springframework.stereotype.Service;

@Service
public class SongMapperService {

    public Song toEntity(SongRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Song song = new Song();
        // ID nie ustawiamy, bo generuje je baza
        song.setTitle(dto.getTitle());
        song.setArtist(dto.getArtist());
        song.setGenre(dto.getGenre());
        song.setLyrics(dto.getLyrics());

        return song;
    }

    public SongResponseDTO toResponseDTO(Song song) {
        if (song == null) {
            return null;
        }

        SongResponseDTO dto = new SongResponseDTO();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setArtist(song.getArtist());
        dto.setGenre(song.getGenre());

        // Nie ustawiamy 'lyrics'
        return dto;
    }

    public Song updateEntityFromDTO(Song song, SongRequestDTO dto) {
        if (song == null || dto == null) {
            return song;
        }

        if (dto.getTitle() != null) {
            song.setTitle(dto.getTitle());
        }
        if (dto.getArtist() != null) {
            song.setArtist(dto.getArtist());
        }
        if (dto.getGenre() != null) {
            song.setGenre(dto.getGenre());
        }
        if (dto.getLyrics() != null) {
            song.setLyrics(dto.getLyrics());
        }

        return song;
    }
}