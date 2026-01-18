package com.karaokeapp.karaoke_backend.services; // Lub .mappers

import com.karaokeapp.karaoke_backend.dto.SongDetailsDTO;
import com.karaokeapp.karaoke_backend.dto.SongRequestDTO;
import com.karaokeapp.karaoke_backend.dto.SongResponseDTO;
import com.karaokeapp.karaoke_backend.models.Author;
import com.karaokeapp.karaoke_backend.models.Song;
import com.karaokeapp.karaoke_backend.models.Category;
import com.karaokeapp.karaoke_backend.repositories.AuthorRepository;
import com.karaokeapp.karaoke_backend.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SongMapperService {

    public SongMapperService() {
    }

    public Song toEntity(SongRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Song song = new Song();
        song.setTitle(dto.getTitle());
        song.setGenre(dto.getGenre());
        song.setLyrics(dto.getLyrics());
        song.setYoutubeUrl(dto.getYoutubeUrl());

        return song;
    }

    public SongResponseDTO toResponseDTO(Song song) {
        if (song == null) {
            return null;
        }

        SongResponseDTO dto = new SongResponseDTO();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        // Nazwy autorów
        if (song.getAuthors() != null) {
            Set<String> authorNames = song.getAuthors().stream()
                    .map(Author::getName)
                    .collect(Collectors.toSet());
            dto.setAuthors(authorNames);
        }

        // Nazwy kategorii
        if (song.getCategories() != null) {
            Set<String> categoryNames = song.getCategories().stream()
                    .map(Category::getName)
                    .collect(Collectors.toSet());
            dto.setCategories(categoryNames);
        }

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

        if (dto.getGenre() != null) {
            song.setGenre(dto.getGenre());
        }
        if (dto.getLyrics() != null) {
            song.setLyrics(dto.getLyrics());
        }
        if (dto.getYoutubeUrl() != null){
            song.setYoutubeUrl(dto.getYoutubeUrl());
        }

        return song;
    }

    public SongDetailsDTO toDetailsDTO(Song song) {
        if (song == null) {
            return null;
        }
        SongDetailsDTO dto = new SongDetailsDTO();

        dto.setId(song.getId());
        dto.setTitle(song.getTitle());

        if (song.getAuthors() != null) {
            Set<String> authorNames = song.getAuthors().stream()
                    .map(Author::getName)
                    .collect(Collectors.toSet());
            dto.setAuthors(authorNames);
        }


        if (song.getCategories() != null) {
            Set<String> categoryNames = song.getCategories().stream()
                    .map(Category::getName)
                    .collect(Collectors.toSet());
            dto.setCategories(categoryNames);
        }

        dto.setGenre(song.getGenre());
        dto.setLyrics(song.getLyrics());
        dto.setYoutubeUrl(song.getYoutubeUrl());

        return dto;
    }
}