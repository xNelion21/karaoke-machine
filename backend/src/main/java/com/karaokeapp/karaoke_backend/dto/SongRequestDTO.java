package com.karaokeapp.karaoke_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SongRequestDTO {

    @NotBlank(message = "Tytuł piosenki jest wymagany")
    @Size(max = 100, message = "Tytuł piosenki jest za długi")
    private String title;

    @NotBlank(message = "Nazwa artysty jest wymagana")
    @Size(max = 100, message = "Nazwa artysty jest za długa")
    private String artist;

    @Size(max = 50, message = "Gatunek jest za długi")
    private String genre;
    private String lyrics;
}