package com.edutrack.controller;

import com.edutrack.dto.request.NoteRequestDTO;
import com.edutrack.dto.response.NoteResponseDTO;
import com.edutrack.service.INoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Slf4j
public class LivreController {

    // INoteService — pas NoteServiceImpl
    // RAPPEL SOLID D : dépendre de l'abstraction
    private final INoteService noteService;

    // ── POST /api/notes ───────────────────────────────────────────────
    // Créer une nouvelle note
    //
    // @PostMapping → cette méthode répond aux requêtes POST sur /api/notes
    //
    // @Valid → active les validations du DTO
    //          Spring vérifie @NotNull, @DecimalMin, @Pattern...
    //          Si invalide → MethodArgumentNotValidException
    //          → GlobalExceptionHandler → Map<champ, message> 400
    //
    // @RequestBody → Spring lit le corps JSON de la requête
    //                et le convertit en NoteRequestDTO automatiquement
    //
    // ResponseEntity<NoteResponseDTO>
    //   → on retourne le code HTTP + le body JSON ensemble
    //   → 201 Created car on a créé une ressource (pas 200 OK)
    @PostMapping
    public ResponseEntity<NoteResponseDTO> ajouterNote(
            @Valid @RequestBody NoteRequestDTO dto) {

        log.info("POST /api/notes — etudiantId={} coursId={}",
                dto.getEtudiantId(), dto.getCoursId());

        NoteResponseDTO note = noteService.ajouterNote(dto);

        // 201 Created → code standard quand on crée une ressource
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(note);
    }

    // ── GET /api/notes/etudiant/3 ─────────────────────────────────────
    // Toutes les notes d'un étudiant
    //
    // @GetMapping → répond aux requêtes GET
    // "/etudiant/{etudiantId}" → {etudiantId} est une variable dans l'URL
    //
    // @PathVariable → extrait la valeur de {etudiantId} dans l'URL
    //   GET /api/notes/etudiant/3 → etudiantId = 3
    //   GET /api/notes/etudiant/7 → etudiantId = 7
    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<NoteResponseDTO>> getNotesByEtudiant(
            @PathVariable Long etudiantId) {

        log.info("GET /api/notes/etudiant/{}", etudiantId);
        return ResponseEntity.ok(
                noteService.getNotesByEtudiant(etudiantId));
    }

    // ── GET /api/notes/etudiant/3/semestre?semestre=2024-S1 ───────────
    // Notes d'un étudiant pour un semestre donné
    //
    // @RequestParam → extrait le paramètre après le ? dans l'URL
    //   ?semestre=2024-S1 → semestre = "2024-S1"
    //
    // Différence PathVariable vs RequestParam :
    //   PathVariable → dans le chemin : /api/notes/etudiant/3
    //   RequestParam → après le ? : ?semestre=2024-S1
    @GetMapping("/etudiant/{etudiantId}/semestre")
    public ResponseEntity<List<NoteResponseDTO>> getNotesBySemestre(
            @PathVariable Long etudiantId,
            @RequestParam String semestre) {

        log.info("GET /api/notes/etudiant/{}/semestre?semestre={}",
                etudiantId, semestre);
        return ResponseEntity.ok(
                noteService.getNotesByEtudiantEtSemestre(
                        etudiantId, semestre));
    }

    // ── GET /api/notes/etudiant/3/moyenne?semestre=2024-S1 ────────────
    // Calcul de la moyenne d'un étudiant pour un semestre
    @GetMapping("/etudiant/{etudiantId}/moyenne")
    public ResponseEntity<Double> getMoyenne(
            @PathVariable Long etudiantId,
            @RequestParam String semestre) {

        log.info("GET moyenne — etudiantId={} semestre={}",
                etudiantId, semestre);
        return ResponseEntity.ok(
                noteService.calculerMoyenne(etudiantId, semestre));
    }

    // ── PUT /api/notes/1 ──────────────────────────────────────────────
    // Modifier une note existante
    //
    // @PutMapping("/{id}") → répond aux PUT sur /api/notes/1
    // On utilise PUT et non PATCH car on remplace les champs modifiables
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> modifierNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequestDTO dto) {

        log.info("PUT /api/notes/{}", id);
        return ResponseEntity.ok(noteService.modifierNote(id, dto));
    }

}
