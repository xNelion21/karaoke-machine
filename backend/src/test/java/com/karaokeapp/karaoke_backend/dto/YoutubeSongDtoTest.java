package com.karaokeapp.karaoke_backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class YoutubeSongDtoTest {

    @Test
    void shouldHandleMissingThumbnail_LikeInDatabase() {
        String videoId = "oKa9tnfZWP0";
        String title = "Chyba że z Tobą";
        String thumbnail = null;
        String lyrics = null;
        String artist = "Modelki";

        YoutubeSongDto songDto = new YoutubeSongDto(videoId, title, thumbnail, lyrics, artist);

        assertEquals("oKa9tnfZWP0", songDto.getVideoId());
        assertNull(songDto.getThumbnailUrl());
        assertNull(songDto.getLyrics());
    }

    @Test
    void shouldCreateCompleteObject_WhenAllDataIsPresent() {
        String expectedId = "testId123";
        String expectedTitle = "Waka Waka";
        String expectedArtist = "Shakira";
        String expectedThumb = "http://img.youtube.com/vi/123/hqdefault.jpg";
        String expectedLyrics = "Tsamina mina eh eh";

        YoutubeSongDto songDto = new YoutubeSongDto(
                expectedId,
                expectedTitle,
                expectedThumb,
                expectedLyrics,
                expectedArtist
        );

        assertEquals(expectedId, songDto.getVideoId());
        assertEquals(expectedTitle, songDto.getTitle());
        assertEquals(expectedArtist, songDto.getArtist());
        assertEquals(expectedThumb, songDto.getThumbnailUrl());
        assertEquals(expectedLyrics, songDto.getLyrics());

        assertFalse(songDto.getTitle().isEmpty());
    }

    @Test
    void shouldAllowSettingValuesViaSetters() {
        YoutubeSongDto songDto = new YoutubeSongDto();

        songDto.setVideoId("xyz");
        songDto.setTitle("Test Title");
        songDto.setArtist("Test Artist");
        songDto.setLyrics("Test Lyrics");

        assertEquals("xyz", songDto.getVideoId());
        assertEquals("Test Title", songDto.getTitle());
        assertEquals("Test Artist", songDto.getArtist());
        assertEquals("Test Lyrics", songDto.getLyrics());
        assertNull(songDto.getThumbnailUrl());
    }
}