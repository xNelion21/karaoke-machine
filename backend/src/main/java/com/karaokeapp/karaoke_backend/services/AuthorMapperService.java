package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.AuthorDTO;
import com.karaokeapp.karaoke_backend.models.Author;
import org.springframework.stereotype.Service;

@Service
public class AuthorMapperService {

    public AuthorDTO toDTO(Author author) {
        if (author == null) {
            return null;
        }
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        return dto;
    }

    public Author toEntity(AuthorDTO dto) {
        if (dto == null) {
            return null;
        }
        Author author = new Author();
        author.setId(dto.getId());
        author.setName(dto.getName());
        return author;
    }

    public Author updateEntityFromDTO(Author author, AuthorDTO dto) {
        if (dto == null || author == null) {
            return author;
        }
        if (dto.getName() != null) {
            author.setName(dto.getName());
        }

        return author;
    }
}
