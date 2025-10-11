package com.karaokeapp.karaoke_backend.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @Size(min = 3, max = 50, message = "Nazwa użytkownika musi mieć od 3 do 50 znaków")
    private String username;
    @Email(message = "Nieprawidłowy format adresu email")
    @Size(max = 100, message = "Email jest za długi")
    private String email;

}