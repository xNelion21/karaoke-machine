package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.SongRequestDTO;
import com.karaokeapp.karaoke_backend.dto.SongResponseDTO;
import com.karaokeapp.karaoke_backend.dto.YoutubeSongDto;
import com.karaokeapp.karaoke_backend.models.Author;
import com.karaokeapp.karaoke_backend.models.Category;
import com.karaokeapp.karaoke_backend.models.Song;
import com.karaokeapp.karaoke_backend.repositories.AuthorRepository;
import com.karaokeapp.karaoke_backend.repositories.CategoryRepository;
import com.karaokeapp.karaoke_backend.repositories.SongRepository;
import com.karaokeapp.karaoke_backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private SongRepository songRepository;
    @Mock
    private SongMapperService songMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SongService songService;


    @Test
    void shouldCreateSong_WhenTitleIsUnique() {
        SongRequestDTO request = new SongRequestDTO();
        request.setTitle("Unikalny Tytuł");

        Song songEntity = new Song();
        songEntity.setTitle("Unikalny Tytuł");

        Song savedSong = new Song();
        savedSong.setId(1L);
        savedSong.setTitle("Unikalny Tytuł");

        SongResponseDTO expectedResponse = new SongResponseDTO();
        expectedResponse.setTitle("Unikalny Tytuł");

        when(songRepository.existsByTitle("Unikalny Tytuł")).thenReturn(false);
        when(songMapper.toEntity(request)).thenReturn(songEntity);
        when(songRepository.save(songEntity)).thenReturn(savedSong);
        when(songMapper.toResponseDTO(savedSong)).thenReturn(expectedResponse);

        SongResponseDTO result = songService.createSong(request);

        assertNotNull(result);
        assertEquals("Unikalny Tytuł", result.getTitle());
        verify(songRepository).save(any(Song.class));
    }

    @Test
    void shouldThrowException_WhenCreatingDuplicateSong() {
        SongRequestDTO request = new SongRequestDTO();
        request.setTitle("Dubel");

        when(songRepository.existsByTitle("Dubel")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            songService.createSong(request);
        });

        assertTrue(exception.getMessage().contains("już istnieje"));
        verify(songRepository, never()).save(any());
    }


    @Test
    void shouldThrowException_WhenSearchQueryIsBlank() {
        assertThrows(RuntimeException.class, () -> {
            songService.searchSongs("", null, null);
        });
    }

    @Test
    void shouldSearchByQueryOnly() {
        String query = "Love";
        Song song = new Song();
        song.setTitle("Love Me Tender");

        when(songRepository.findByTitleContainingIgnoreCase(query)).thenReturn(List.of(song));
        when(songMapper.toResponseDTO(song)).thenReturn(new SongResponseDTO());

        List<SongResponseDTO> result = songService.searchSongs(query, null, null);

        assertFalse(result.isEmpty());
        verify(songRepository).findByTitleContainingIgnoreCase(query);
    }


    @Test
    void shouldImportNewSongFromYoutube_AndCreateAuthorAndCategory() {
        YoutubeSongDto ytDto = new YoutubeSongDto();
        ytDto.setVideoId("vid123");
        ytDto.setTitle("Nowy Hit");
        ytDto.setArtist("Nowy Artysta");
        ytDto.setLyrics("La la la");

        String expectedUrl = "https://www.youtube.com/watch?v=vid123";

        when(songRepository.findByYoutubeUrl(expectedUrl)).thenReturn(Optional.empty());

        Category defaultCategory = new Category();
        defaultCategory.setId(3L);
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(defaultCategory));

        when(authorRepository.findByName("Nowy Artysta")).thenReturn(Optional.empty());
        when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(songRepository.save(any(Song.class))).thenAnswer(invocation -> {
            Song s = invocation.getArgument(0);
            s.setId(100L);
            return s;
        });

        when(songMapper.toResponseDTO(any())).thenReturn(new SongResponseDTO());

        SongResponseDTO result = songService.importFromYoutube(ytDto);

        assertNotNull(result);

        verify(songRepository).save(argThat(song ->
                song.getTitle().equals("Nowy Hit") &&
                        song.getYoutubeUrl().equals(expectedUrl) &&
                        song.getCategories().contains(defaultCategory) &&
                        song.getAuthors().stream().anyMatch(a -> a.getName().equals("Nowy Artysta"))
        ));
    }

    @Test
    void shouldReturnExistingSong_WhenImportingKnownYoutubeVideo() {
        YoutubeSongDto ytDto = new YoutubeSongDto();
        ytDto.setVideoId("existing123");
        String fullUrl = "https://www.youtube.com/watch?v=existing123";

        Song existingSong = new Song();
        existingSong.setId(5L);
        existingSong.setYoutubeUrl(fullUrl);

        when(songRepository.findByYoutubeUrl(fullUrl)).thenReturn(Optional.of(existingSong));
        when(songMapper.toResponseDTO(existingSong)).thenReturn(new SongResponseDTO());

        songService.importFromYoutube(ytDto);

        verify(songRepository, never()).save(any());
        verify(categoryRepository, never()).findById(any());
    }
}