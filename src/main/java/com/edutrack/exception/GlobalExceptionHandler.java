package com.edutrack.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


// ════════════════════════════════════════════════════════════════════
// VERSION HYBRIDE — meilleure pour Angular
//
// 404/400/500 → ErrorResponse {status, message, timestamp}
//   Angular lit error.message pour afficher une alerte globale
//
// Validation @Valid → Map<String, String> {champ: message}
//   Angular lit errors['valeur'] pour afficher sous le bon input
// ════════════════════════════════════════════════════════════════════
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── 404 — entités introuvables ────────────────────────────────────
    @ExceptionHandler({
            NoteNotFoundException.class,
            EtudiantNotFoundException.class,
            CoursNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(
            RuntimeException ex) {
        log.warn("Ressource introuvable : {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    // ── 400 — règle métier violée ─────────────────────────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleMetier(
            IllegalArgumentException ex) {
        log.warn("Erreur metier : {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    // ── 400 — validation @Valid — Map pour Angular ────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> erreurs = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            erreurs.put(fe.getField(), fe.getDefaultMessage());
        }
        log.warn("Validation echouee : {}", erreurs);
        return ResponseEntity.badRequest().body(erreurs);
    }

    // ── 500 — erreur inattendue ───────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Erreur inattendue : {}", ex.getMessage());
        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Erreur interne — contactez l'administrateur",
                        LocalDateTime.now()
                ));
    }
}
