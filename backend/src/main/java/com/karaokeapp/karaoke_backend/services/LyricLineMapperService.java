package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.LyricLineDTO;
import com.karaokeapp.karaoke_backend.models.LyricLine;
import org.springframework.stereotype.Service;

@Service
public class LyricLineMapperService {

    public LyricLineDTO toDTO(LyricLine lyricLine) {
        LyricLineDTO dto = new LyricLineDTO();
        dto.setId(lyricLine.getId());
        dto.setText(lyricLine.getText());
        dto.setTimeStampStart(lyricLine.getTimeStampStart());
        dto.setTimeStampEnd(lyricLine.getTimeStampEnd());
        return dto;
    }

    public LyricLine toEntity(LyricLineDTO dto) {
        LyricLine lyricLine = new LyricLine();
        lyricLine.setText(dto.getText());
        lyricLine.setTimeStampStart(dto.getTimeStampStart());
        lyricLine.setTimeStampEnd(dto.getTimeStampEnd());
        return lyricLine;
    }
}
