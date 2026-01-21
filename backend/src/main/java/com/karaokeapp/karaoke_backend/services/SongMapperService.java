package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.SongDetailsDTO;
import com.karaokeapp.karaoke_backend.dto.SongRequestDTO;
import com.karaokeapp.karaoke_backend.dto.SongResponseDTO;
import com.karaokeapp.karaoke_backend.models.Author;
import com.karaokeapp.karaoke_backend.models.Category;
import com.karaokeapp.karaoke_backend.models.Song;
import com.karaokeapp.karaoke_backend.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongMapperService {

    private final CategoryRepository categoryRepository;

    public SongResponseDTO toResponseDTO(Song song) {
        if (song == null) return null;
        SongResponseDTO dto = new SongResponseDTO();
        mapCommonFields(song, dto);
        return dto;
    }

    public SongDetailsDTO toDetailsDTO(Song song) {
        if (song == null) return null;
        SongDetailsDTO dto = new SongDetailsDTO();
        mapCommonFields(song, dto); // Używamy tej samej metody co wyżej
        dto.setLyrics(song.getLyrics());
        dto.setYoutubeUrl(song.getYoutubeUrl());
        return dto;
    }

    public Song updateEntityFromDTO(Song song, SongRequestDTO dto) {
        if (song == null || dto == null) return song;

        if (dto.getTitle() != null) song.setTitle(dto.getTitle());
        if (dto.getLyrics() != null) song.setLyrics(dto.getLyrics());
        if (dto.getYoutubeUrl() != null) song.setYoutubeUrl(dto.getYoutubeUrl());

        if (dto.getCategories() != null) {
            updateSongCategories(song, dto.getCategories());
        }

        return song;
    }

    public Song toEntity(SongRequestDTO dto) {
        if (dto == null) return null;

        Song song = new Song();
        song.setTitle(dto.getTitle());
        song.setLyrics(dto.getLyrics());
        song.setYoutubeUrl(dto.getYoutubeUrl());

        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            updateSongCategories(song, dto.getCategories());
        }

        return song;
    }

    // --- PRYWATNE METODY POMOCNICZE (Wspólna logika) ---

    private void mapCommonFields(Song song, Object dto) {


        if (dto instanceof SongResponseDTO responseDTO) {
            responseDTO.setId(song.getId());
            responseDTO.setTitle(song.getTitle());
            responseDTO.setAuthors(getAuthorNames(song));
            responseDTO.setCategories(getCategoryNames(song));
        } else if (dto instanceof SongDetailsDTO detailsDTO) {
            detailsDTO.setId(song.getId());
            detailsDTO.setTitle(song.getTitle());
            detailsDTO.setAuthors(getAuthorNames(song));
            detailsDTO.setCategories(getCategoryNames(song));
        }
    }

    private Set<String> getAuthorNames(Song song) {
        if (song.getAuthors() == null) return new HashSet<>();
        return song.getAuthors().stream().map(Author::getName).collect(Collectors.toSet());
    }

    private Set<String> getCategoryNames(Song song) {
        if (song.getCategories() == null) return new HashSet<>();
        return song.getCategories().stream().map(Category::getName).collect(Collectors.toSet());
    }

    private void updateSongCategories(Song song, Set<String> categoryNames) {
        song.getCategories().clear();
        Set<Category> newCategories = categoryNames.stream()
                .map(name -> categoryRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono kategorii: " + name)))
                .collect(Collectors.toSet());
        song.getCategories().addAll(newCategories);
    }
}