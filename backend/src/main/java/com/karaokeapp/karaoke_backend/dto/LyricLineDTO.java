package com.karaokeapp.karaoke_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LyricLineDTO {

    private Long id;

    @NotBlank(message = "Linia tekstu nie może być pusta")
    private String text;

    private double timeStampStart;

    private double timeStampEnd;
}
