package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.YoutubeSongDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class YoutubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    private final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/search";

    public List<YoutubeSongDto> searchSongs(String query) {
        RestTemplate restTemplate = new RestTemplate();

        String url = String.format("%s?part=snippet&q=%s+karaoke&type=video&maxResults=10&key=%s",
                YOUTUBE_API_URL, query, apiKey);

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            List<YoutubeSongDto> songs = new ArrayList<>();

            if (response != null && response.containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

                for (Map<String, Object> item : items) {
                    Map<String, Object> idMap = (Map<String, Object>) item.get("id");
                    Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                    Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
                    Map<String, Object> mediumThumb = (Map<String, Object>) thumbnails.get("medium");

                    songs.add(new YoutubeSongDto(
                            (String) idMap.get("videoId"),
                            (String) snippet.get("title"),
                            (String) mediumThumb.get("url")
                    ));
                }
            }
            return songs;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}