package com.karaokeapp.karaoke_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

import java.util.Set;

@Data
public class SongRequestDTO {

    @NotBlank(message = "Tytuł piosenki jest wymagany")
    @Size(max = 100, message = "Tytuł piosenki jest za długi")
    private String title;

    @Size(max = 50, message = "Gatunek jest za długi")
    private Set<String> categories;
    private String lyrics;
    private String youtubeUrl;


}