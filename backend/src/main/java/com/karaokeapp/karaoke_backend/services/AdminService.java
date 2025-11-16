package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.models.Song;
import com.karaokeapp.karaoke_backend.models.Suggestion;
import com.karaokeapp.karaoke_backend.models.SuggestionStatus;
import com.karaokeapp.karaoke_backend.repositories.SongRepository;
import com.karaokeapp.karaoke_backend.repositories.SuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final SuggestionRepository suggestionRepository;
    private final SongRepository songRepository;


    public List<Suggestion> getPendingSuggestions() {
        return suggestionRepository.findAllByStatus(SuggestionStatus.PENDING);
    }


    public void processSuggestion(Long suggestionId, SuggestionStatus newStatus) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Propozycja o ID " + suggestionId + " nie istnieje."));

        suggestion.setStatus(newStatus);
        suggestionRepository.save(suggestion);

        if (newStatus == SuggestionStatus.ACCEPTED) {
            Song song = suggestion.getSong();

            song.setLyrics(suggestion.getProposedContent());
            songRepository.save(song);
        }
    }
}