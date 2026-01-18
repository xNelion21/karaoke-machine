package com.karaokeapp.karaoke_backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacja do piosenki, której dotyczy propozycja
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    // Relacja do użytkownika, który zgłosił propozycję
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    private String proposedContent;

    @Enumerated(EnumType.STRING)
    private SuggestionStatus status = SuggestionStatus.PENDING;

    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();


    @Lob
    private String proposedLyrics;
    private String proposedGenre;

}