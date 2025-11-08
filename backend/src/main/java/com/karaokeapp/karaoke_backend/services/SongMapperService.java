package com.karaokeapp.karaoke_backend.services; // Lub .mappers

import com.karaokeapp.karaoke_backend.dto.SongRequestDTO;
import com.karaokeapp.karaoke_backend.dto.SongResponseDTO;
import com.karaokeapp.karaoke_backend.models.Author;
import com.karaokeapp.karaoke_backend.models.Song; // Encja Song (założenie, że Marcin ją stworzył)
import com.karaokeapp.karaoke_backend.models.Category;
import com.karaokeapp.karaoke_backend.repositories.AuthorRepository;
import com.karaokeapp.karaoke_backend.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SongMapperService {

    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    public SongMapperService(AuthorRepository authorRepository, CategoryRepository categoryRepository) {
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
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

        return song;
    }
}