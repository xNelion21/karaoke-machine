package com.karaokeapp.karaoke_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class SuggestionRequestDTO {

    private Long songId;
    private Set<String> currentCategories;
    private YoutubeSongDto youtubeSongData;
    private String proposedLyrics;
    private Set<Long> proposedAuthorIds;
    private Set<Long> proposedCategoryIds;
    private String proposedContent;


}