package com.karaokeapp.karaoke_backend.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LyricLineDTO {

    private Long id;

    @NotBlank(message = "Linia tekstu nie może być pusta")
    private String text;

    @NotNull(message = "Czas startu jest wymagany")
    private Double timeStampStart;

    @NotNull(message = "Czas zakończenia jest wymagany")
    private Double timeStampEnd;

    @SuppressWarnings("unused")
    @AssertTrue(message = "Czas zakończenia musi być późniejszy niż czas rozpoczęcia")
    public boolean isTimeValid(){
        if(timeStampStart == null || timeStampEnd == null){
            return true;
        }
        return timeStampStart >= timeStampEnd;
    }
}
