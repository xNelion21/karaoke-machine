package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.SongDetailsDTO;
import com.karaokeapp.karaoke_backend.dto.SongRequestDTO;
import com.karaokeapp.karaoke_backend.dto.SongResponseDTO;
import com.karaokeapp.karaoke_backend.models.Author;
import com.karaokeapp.karaoke_backend.models.Category;
import com.karaokeapp.karaoke_backend.models.Song;
import com.karaokeapp.karaoke_backend.repositories.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongMapperServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private SongMapperService mapper;


    @Test
    void shouldMapEntityToResponseDTO() {
        Song song = createTestSong();

        SongResponseDTO result = mapper.toResponseDTO(song);

        assertNotNull(result);
        assertEquals(song.getId(), result.getId());
        assertEquals(song.getTitle(), result.getTitle());
        assertTrue(result.getAuthors().contains("Krawczyk"));
        assertTrue(result.getCategories().contains("Pop"));
    }

    @Test
    void shouldMapEntityToDetailsDTO() {
        Song song = createTestSong();

        SongDetailsDTO result = mapper.toDetailsDTO(song);

        assertNotNull(result);
        assertEquals(song.getTitle(), result.getTitle());
        assertEquals("La la la", result.getLyrics());
        assertEquals("http://yt.com", result.getYoutubeUrl());
    }

    @Test
    void shouldMapRequestToEntity_AndFetchCategories() {
        SongRequestDTO request = new SongRequestDTO();
        request.setTitle("Nowa Piosenka");
        request.setCategories(Set.of("Rock"));

        Category rockCategory = new Category();
        rockCategory.setName("Rock");

        when(categoryRepository.findByName("Rock")).thenReturn(Optional.of(rockCategory));

        Song result = mapper.toEntity(request);

        assertNotNull(result);
        assertEquals("Nowa Piosenka", result.getTitle());
        assertEquals(1, result.getCategories().size());
        assertTrue(result.getCategories().contains(rockCategory));
    }

    @Test
    void shouldThrowException_WhenCategoryNotFound() {
        SongRequestDTO request = new SongRequestDTO();
        request.setTitle("Błędna Piosenka");
        request.setCategories(Set.of("NieznanaKategoria"));

        when(categoryRepository.findByName("NieznanaKategoria")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            mapper.toEntity(request);
        });

        assertEquals("Nie znaleziono kategorii: NieznanaKategoria", exception.getMessage());
    }

    @Test
    void shouldUpdateExistingEntity() {
        Song existingSong = createTestSong();

        SongRequestDTO updateRequest = new SongRequestDTO();
        updateRequest.setTitle("Zmieniony Tytuł");
        updateRequest.setCategories(Set.of("Jazz"));

        Category jazzCategory = new Category();
        jazzCategory.setName("Jazz");

        when(categoryRepository.findByName("Jazz")).thenReturn(Optional.of(jazzCategory));

        Song updatedSong = mapper.updateEntityFromDTO(existingSong, updateRequest);

        assertEquals("Zmieniony Tytuł", updatedSong.getTitle());
        assertEquals(1, updatedSong.getCategories().size());
        assertEquals("Jazz", updatedSong.getCategories().iterator().next().getName());
    }


    private Song createTestSong() {
        Song song = new Song();
        song.setId(1L);
        song.setTitle("Parostatek");
        song.setLyrics("La la la");
        song.setYoutubeUrl("http://yt.com");

        Author author = new Author();
        author.setName("Krawczyk");
        song.setAuthors(new HashSet<>(Collections.singletonList(author)));

        Category category = new Category();
        category.setName("Pop");
        song.setCategories(new HashSet<>(Collections.singletonList(category)));

        return song;
    }
}