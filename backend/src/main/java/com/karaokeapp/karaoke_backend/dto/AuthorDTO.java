package com.karaokeapp.karaoke_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorDTO {
    private Long id;
    @NotBlank(message = "Nazwa autora jest wymagana")
    private String name;
}
