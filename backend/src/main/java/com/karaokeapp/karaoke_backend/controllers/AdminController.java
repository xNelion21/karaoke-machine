package com.karaokeapp.karaoke_backend.controllers;
import com.karaokeapp.karaoke_backend.dto.UserResponseDTO;
import com.karaokeapp.karaoke_backend.models.Suggestion;
import com.karaokeapp.karaoke_backend.models.SuggestionStatus;
import com.karaokeapp.karaoke_backend.services.AdminService;
import com.karaokeapp.karaoke_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/suggestions/pending")
    public ResponseEntity<List<Suggestion>> getPendingSuggestions() {
        List<Suggestion> suggestions = adminService.getPendingSuggestions();
        return ResponseEntity.ok(suggestions);
    }

    @PutMapping("/users/{id}/lock")
    public ResponseEntity<UserResponseDTO> lockUser(
            @PathVariable Long id,
            @RequestParam boolean status) {

        UserResponseDTO updatedUser = userService.lockUser(id, status);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/suggestions/{id}/status")
    public ResponseEntity<Void> processSuggestion(
            @PathVariable Long id,
            @RequestParam String action) {

        SuggestionStatus newStatus;
        try {
            newStatus = SuggestionStatus.valueOf(action.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        if (newStatus == SuggestionStatus.ACCEPTED || newStatus == SuggestionStatus.REJECTED) {
            adminService.processSuggestion(id, newStatus);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}