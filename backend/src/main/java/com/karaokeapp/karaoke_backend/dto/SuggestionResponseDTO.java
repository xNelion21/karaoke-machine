package com.karaokeapp.karaoke_backend.dto;

import com.karaokeapp.karaoke_backend.models.SuggestionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class SuggestionResponseDTO {
    private Long id;
    private Long songId;
    private String songTitle;
    private String currentLyrics;
    private Set<String> currentCategories;

    private Long userId;
    private String username;
    private String proposedLyrics;
    private Set<Long> proposedAuthorIds;
    private Set<Long> proposedCategoryIds;
    private String proposedContent;

    private SuggestionStatus status;
    private LocalDateTime createdAt;
}