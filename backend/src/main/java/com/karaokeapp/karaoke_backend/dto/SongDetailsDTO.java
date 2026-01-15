package com.karaokeapp.karaoke_backend.dto;

import lombok.Data;

import java.util.Set;

@Data
public class SongDetailsDTO {
    private Long id;
    private String title;
    private String genre;
    private String lyrics;
    private Set<String> authors;
    private Set<String> categories;
    private String youtubeUrl;
}
