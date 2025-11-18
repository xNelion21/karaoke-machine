package com.karaokeapp.karaoke_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class SongDetailsDTO {
    private SongResponseDTO song;
    private List<LyricLineDTO> lyricLines;
    private String youtubeUrl;
}
