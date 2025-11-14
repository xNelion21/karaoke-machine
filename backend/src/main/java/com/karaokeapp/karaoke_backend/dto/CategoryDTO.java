package com.karaokeapp.karaoke_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;
    @NotBlank(message = "Nazwa kategorii jest wymagana")
    private String name;
}
