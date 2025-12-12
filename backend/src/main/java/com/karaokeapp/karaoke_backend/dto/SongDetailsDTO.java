package com.karaokeapp.karaoke_backend.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class SongDetailsDTO {
    private Long id;
    private String title;
    private String genre;
    private Set<String> authors;
    private Set<String> categories;
    private List<LyricLineDTO> lyricLines;
    private String youtubeUrl;
}
