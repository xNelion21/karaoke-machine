package com.karaokeapp.karaoke_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YoutubeSongDto {
    private String videoId;
    private String title;
    private String thumbnailUrl;
}