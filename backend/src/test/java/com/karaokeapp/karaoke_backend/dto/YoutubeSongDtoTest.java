package com.karaokeapp.karaoke_backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class YoutubeSongDtoTest {

    @Test
    void shouldHandleMissingThumbnail_LikeInDatabase() {

        String videoId = "oKa9tnfZWP0";
        String title = "Chyba że z Tobą";
        String thumbnail = null;
        String artist = "Modelki";


        YoutubeSongDto songDto = new YoutubeSongDto(videoId, title, thumbnail, artist);


        assertEquals("oKa9tnfZWP0", songDto.getVideoId());
        assertNull(songDto.getThumbnailUrl());
    }

    @Test
    void shouldCreateCompleteObject_WhenAllDataIsPresent() {

        String expectedId = "testId123";
        String expectedTitle = "Waka Waka";
        String expectedArtist = "Shakira";
        String expectedThumb = "http://img.youtube.com/vi/123/hqdefault.jpg";

        YoutubeSongDto songDto = new YoutubeSongDto(
                expectedId,
                expectedTitle,
                expectedThumb,
                expectedArtist
        );

        assertEquals(expectedId, songDto.getVideoId());
        assertEquals(expectedTitle, songDto.getTitle());
        assertEquals(expectedArtist, songDto.getArtist());
        assertEquals(expectedThumb, songDto.getThumbnailUrl());

        assertFalse(songDto.getTitle().isEmpty());
    }

    @Test
    void shouldAllowSettingValuesViaSetters() {

        YoutubeSongDto songDto = new YoutubeSongDto();

        songDto.setVideoId("xyz");
        songDto.setTitle("Test Title");
        songDto.setArtist("Test Artist");

        assertEquals("xyz", songDto.getVideoId());
        assertEquals("Test Title", songDto.getTitle());
        assertEquals("Test Artist", songDto.getArtist());
        assertNull(songDto.getThumbnailUrl());
    }
}