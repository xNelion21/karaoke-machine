package com.karaokeapp.karaoke_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SuggestionRequestDTO {

    @NotNull(message = "ID piosenki jest wymagane")
    private Long songId;

    @NotBlank(message = "Treść propozycji jest wymagana")
    private String proposedContent;
}