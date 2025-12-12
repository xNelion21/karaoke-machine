package com.karaokeapp.karaoke_backend.services; // Lub .mappers

import com.karaokeapp.karaoke_backend.dto.LyricLineDTO;
import com.karaokeapp.karaoke_backend.dto.SongDetailsDTO;
import com.karaokeapp.karaoke_backend.dto.SongRequestDTO;
import com.karaokeapp.karaoke_backend.dto.SongResponseDTO;
import com.karaokeapp.karaoke_backend.models.Author;
import com.karaokeapp.karaoke_backend.models.LyricLine;
import com.karaokeapp.karaoke_backend.models.Song; // Encja Song (założenie, że Marcin ją stworzył)
import com.karaokeapp.karaoke_backend.models.Category;
import com.karaokeapp.karaoke_backend.repositories.AuthorRepository;
import com.karaokeapp.karaoke_backend.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SongMapperService {

    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final LyricLineMapperService lyricLineMapperService;

    public SongMapperService(AuthorRepository authorRepository, CategoryRepository categoryRepository, LyricLineMapperService lyricLineMapperService) {
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.lyricLineMapperService = lyricLineMapperService;
    }

    public Song toEntity(SongRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Song song = new Song();
        // ID nie ustawiamy, bo generuje je baza
        song.setTitle(dto.getTitle());

        // Pobierz autorów po ID
        if (dto.getAuthorIds() != null && !dto.getAuthorIds().isEmpty()) {
            Set<Author> authors = dto.getAuthorIds().stream()
                    .map(id -> authorRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Autor o ID " + id + " nie istnieje")))
                    .collect(Collectors.toSet());
            song.setAuthors(authors);
        }

        // Pobierz kategorie po ID
        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            Set<Category> categories = dto.getCategoryIds().stream()
                    .map(id -> categoryRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Kategoria o ID " + id + " nie istnieje")))
                    .collect(Collectors.toSet());
            song.setCategories(categories);
        }

        song.setGenre(dto.getGenre());
        song.setLyrics(dto.getLyrics());
        song.setYoutubeUrl(dto.getYoutubeUrl());

        if (dto.getLyricLines() != null){
            List<LyricLine> lyricLines = dto.getLyricLines().stream()
                    .map(lyricLineMapperService::toEntity)
                    .toList();

            lyricLines.forEach(lyricLine -> lyricLine.setSong(song));

            song.setLyricLines(lyricLines);
        }

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
        // Aktualizacja autorów
        if (dto.getAuthorIds() != null) {
            Set<Author> authors = dto.getAuthorIds().stream()
                    .map(id -> authorRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Autor o ID " + id + " nie istnieje")))
                    .collect(Collectors.toSet());
            song.setAuthors(authors);
        }
        // Aktualizacja kategorii
        if (dto.getCategoryIds() != null) {
            Set<Category> categories = dto.getCategoryIds().stream()
                    .map(id -> categoryRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Kategoria o ID " + id + " nie istnieje")))
                    .collect(Collectors.toSet());
            song.setCategories(categories);
        }
        if (dto.getGenre() != null) {
            song.setGenre(dto.getGenre());
        }
        if (dto.getLyrics() != null) {
            song.setLyrics(dto.getLyrics());
        }
        if (dto.getLyricLines() != null){
            song.getLyricLines().clear();

            for (LyricLineDTO lyricLineDTO : dto.getLyricLines()) {
                LyricLine lyricLine = lyricLineMapperService.toEntity(lyricLineDTO);
                lyricLine.setSong(song);
                song.getLyricLines().add(lyricLine);
            }
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

        dto.setLyricLines(
                song.getLyricLines().stream()
                        .sorted(Comparator.comparing(LyricLine::getTimeStampStart))
                        .map(lyricLineMapperService::toDTO)
                        .toList()
        );

        dto.setYoutubeUrl(song.getYoutubeUrl());

        return dto;
    }
}