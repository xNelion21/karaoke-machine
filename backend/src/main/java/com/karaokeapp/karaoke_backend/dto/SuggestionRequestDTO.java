package com.karaokeapp.karaoke_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class SuggestionRequestDTO {

    @NotNull(message = "ID piosenki jest wymagane")
    private Long songId;
    private String proposedLyrics;
    private String proposedGenre;
    private Set<Long> proposedAuthorIds;
    private Set<Long> proposedCategoryIds;
    private String proposedContent;

}