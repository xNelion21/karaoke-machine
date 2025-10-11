package com.karaokeapp.karaoke_backend.dto;

import lombok.Data;

@Data
public class SongResponseDTO {

    private Long id;
    private String title;
    private String artist;
    private String genre;

}