package com.karaokeapp.karaoke_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "song")
@Entity
@Table(name = "lyric_line")
public class LyricLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String text;

    @Column(nullable = false)
    private double timeStampStart;

    @Column(nullable = false)
    private double timeStampEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    @JsonIgnore // song zwracalo lyricline a lyricline song (petla przy wywolywaniu my-liked-songs)
    private Song song;

}
