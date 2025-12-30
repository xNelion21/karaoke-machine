package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.YoutubeSongDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

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
                    String rawTitle = (String) snippet.get("title");

                    String decodedTitle = HtmlUtils.htmlUnescape(rawTitle);

                    String artist = null;
                    String title = decodedTitle;

                    if (decodedTitle.contains(" - ")) {
                        String[] parts = decodedTitle.split(" - ", 2);
                        artist = parts[0].trim();

                        if (parts.length > 1) {
                            title = parts[1].trim();
                        }
                    }

                    YoutubeSongDto dto = new YoutubeSongDto();
                    dto.setVideoId((String) idMap.get("videoId"));
                    dto.setTitle(title);
                    dto.setArtist(artist);

                    if (mediumThumb != null) {
                        dto.setThumbnailUrl((String) mediumThumb.get("url"));
                    }

                    songs.add(dto);
                }
            }
            return songs;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}