package com.karaokeapp.karaoke_backend.controllers;

import com.karaokeapp.karaoke_backend.dto.UserResponseDTO;
import com.karaokeapp.karaoke_backend.dto.UserUpdateDTO;
import com.karaokeapp.karaoke_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        UserResponseDTO userProfile = userService.getProfileByUsername(username);
        return ResponseEntity.ok(userProfile);
    }

    // 2. AKTUALIZACJA PROFILU (PUT /api/users/{id})
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {

        UserResponseDTO updatedUser = userService.updateProfile(id, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // ... Dalsze endpointy - zmiany hasla i inne cuda

    //polubione
    @PostMapping("/me/likes/{songId}")
    public ResponseEntity<Void> addLikedSong(@PathVariable Long songId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.addLikedSong(username, songId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/me/likes/{songId}")
    public ResponseEntity<Void> removeLikedSong(@PathVariable Long songId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.removeLikedSong(username, songId);
        return ResponseEntity.ok().build();
    }


}