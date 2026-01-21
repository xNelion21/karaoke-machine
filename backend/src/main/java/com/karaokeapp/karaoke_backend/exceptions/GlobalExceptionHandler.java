package com.karaokeapp.karaoke_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<String> handleEmptyBody(HttpMessageNotReadableException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Błąd: Body zapytania jest puste lub niepoprawne. Wyślij dane piosenki w formacie JSON.");
        }

        @ExceptionHandler(LockedException.class)
        public ResponseEntity<?> handleLockedException(LockedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "USER_LOCKED",
                            "message", "Twoje konto jest zablokowane. Skontaktuj się z administratorem."
                    ));
        }
    }



