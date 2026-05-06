package com.edutrack.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
// ════════════════════════════════════════════════════════════════════
// Ce DTO définit EXACTEMENT ce que le professeur envoie
// pour attribuer une note
//
// Ce qui N'EST PAS ici (et pourquoi) :
//   - id         → généré par MySQL automatiquement
//   - dateNote   → @PrePersist la remplit automatiquement
//   - etudiant   → on envoie l'id, pas tout l'objet
//   - cours      → idem, seulement l'id
//
// RAPPEL SOLID S :
// Ce DTO n'a qu'une seule responsabilité :
// représenter les données d'une note à la création
// ════════════════════════════════════════════════════════════════════
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteRequestDTO {

    // @DecimalMin et @DecimalMax vérifient les limites numériques
    // inclusive = true → la valeur 0.0 ET 20.0 sont acceptées
    @NotNull(message = "La valeur est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "La note minimum est 0")
    @DecimalMax(value = "20.0", inclusive = true,
            message = "La note maximum est 20")
    private Double valeur;

    // Format attendu : "2024-S1", "2024-S2"
    // @Pattern vérifie le format avec une expression régulière
    // [0-9]{4} = exactement 4 chiffres
    // -S = tiret suivi de S
    // [12] = soit 1 soit 2
    @NotBlank(message = "Le semestre est obligatoire")
    @Pattern(
            regexp = "[0-9]{4}-S[12]",
            message = "Format semestre invalide — exemple : 2024-S1"
    )
    private String semestre;

    @Column(length = 300)
    private String commentaire;

    // On envoie les ids — pas les objets entiers
    // Le Service ira chercher Etudiant et Cours dans la BDD
    // via leur id
    @NotNull(message = "L'id de l'étudiant est obligatoire")
    private Long etudiantId;

    @NotNull(message = "L'id du cours est obligatoire")
    private Long coursId;
}
