package com.karaokeapp.karaoke_backend.dto;

import lombok.Data;

import java.util.Set;

@Data
public class SongResponseDTO {

    private Long id;
    private String title;
    private Set<String> authors;
    private Set<String> categories;
    private String genre;

}