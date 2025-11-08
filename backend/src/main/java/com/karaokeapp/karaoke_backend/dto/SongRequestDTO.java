package com.karaokeapp.karaoke_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SongRequestDTO {

    @NotBlank(message = "Tytuł piosenki jest wymagany")
    @Size(max = 100, message = "Tytuł piosenki jest za długi")
    private String title;

    @NotBlank(message = "Nazwa artysty jest wymagana")
    private Set<Long> authorsIds;

    @Size(max = 50, message = "Gatunek jest za długi")
    private String genre;
    private String lyrics;

    private Set<Long> categoryIds;

    public Set<Long> getAuthorIds() {
        return authorsIds;
    }

    public Set<Long> getCategoryIds() {
        return categoryIds;
    }
}