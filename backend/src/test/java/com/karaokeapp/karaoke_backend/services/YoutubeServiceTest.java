package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.YoutubeSongDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YoutubeServiceTest {

    @InjectMocks
    private YoutubeService youtubeService;

    @Test
    void shouldReturnSongs_WithoutTouchingServiceCode() {

        Map<String, Object> fakeResponse = createFakeYoutubeResponse();

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.getForObject(anyString(), eq(Map.class))).thenReturn(fakeResponse);
                })) {

            List<YoutubeSongDto> result = youtubeService.searchSongs("queen");

            assertNotNull(result);
            assertEquals(1, result.size());

            YoutubeSongDto song = result.get(0);
            assertEquals("Queen", song.getArtist());
            assertEquals("Bohemian Rhapsody", song.getTitle());
            assertEquals("testId123", song.getVideoId());

            assertNull(song.getLyrics(), "Tekst piosenki w wynikach wyszukiwania powinien być domyślnie null");
        }
    }

    private Map<String, Object> createFakeYoutubeResponse() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();

        Map<String, Object> idMap = new HashMap<>();
        idMap.put("videoId", "testId123");
        item.put("id", idMap);

        Map<String, Object> snippet = new HashMap<>();
        snippet.put("title", "Queen - Bohemian Rhapsody");

        Map<String, Object> thumbnails = new HashMap<>();
        Map<String, Object> mediumThumb = new HashMap<>();
        mediumThumb.put("url", "http://fake.url");
        thumbnails.put("medium", mediumThumb);
        snippet.put("thumbnails", thumbnails);

        item.put("snippet", snippet);
        items.add(item);
        response.put("items", items);

        return response;
    }
}