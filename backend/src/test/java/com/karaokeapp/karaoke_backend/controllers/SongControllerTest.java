package com.karaokeapp.karaoke_backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karaokeapp.karaoke_backend.dto.SongDetailsDTO;
import com.karaokeapp.karaoke_backend.dto.SongResponseDTO;
import com.karaokeapp.karaoke_backend.dto.YoutubeSongDto;
import com.karaokeapp.karaoke_backend.services.JwtService;
import com.karaokeapp.karaoke_backend.services.SongService;
import com.karaokeapp.karaoke_backend.services.YoutubeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SongController.class)
@AutoConfigureMockMvc(addFilters = false)
class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SongService songService;

    @MockBean
    private YoutubeService youtubeService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;


    @Test
    void shouldReturnAllSongs() throws Exception {
        SongResponseDTO song = new SongResponseDTO();
        song.setTitle("Test Song");
        when(songService.getAllSongs()).thenReturn(List.of(song));

        mockMvc.perform(get("/api/songs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Song"));
    }

    @Test
    void shouldReturnSongById() throws Exception {
        SongDetailsDTO details = new SongDetailsDTO();
        details.setId(1L);
        details.setTitle("Szczegóły");
        when(songService.getSongById(1L)).thenReturn(details);

        mockMvc.perform(get("/api/songs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Szczegóły"));
    }


    @Test
    void shouldCreateSongFromYoutube() throws Exception {
        YoutubeSongDto requestDto = new YoutubeSongDto();
        requestDto.setTitle("Nowy Hit");
        requestDto.setVideoId("123");

        SongResponseDTO responseDto = new SongResponseDTO();
        responseDto.setId(10L);
        responseDto.setTitle("Nowy Hit");

        when(songService.importFromYoutube(any(YoutubeSongDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Nowy Hit"));
    }


    @Test
    void shouldDeleteSong() throws Exception {
        mockMvc.perform(delete("/api/songs/1"))
                .andExpect(status().isNoContent());
    }


    @Test
    void shouldSearchOnlineUsingYoutubeService() throws Exception {
        YoutubeSongDto ytResult = new YoutubeSongDto();
        ytResult.setTitle("Wynik z YT");
        when(youtubeService.searchSongs("query")).thenReturn(List.of(ytResult));

        mockMvc.perform(get("/api/songs/search-online")
                        .param("query", "query"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Wynik z YT"));
    }
}