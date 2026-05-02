package com.edutrack.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.engine.internal.Nullability;

import java.time.LocalDate;

@Entity
@Table(name = "notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Valeur entre 0 et 20
    @NotNull(message = "La valeur est obligatoire")
    @DecimalMin(value = "0.0", message = "Note minimum : 0")
    @DecimalMax(value = "20.0", message = "Note maximum : 20")
    @Column(nullable = false)
    private Double valeur;

    // Format : "2024-S1", "2024-S2", "2025-S1"
    // Ce champ sera la BASE du partitionnement BDD (Module 13)
    @NotBlank(message = "Le semestre est obligatoire")
    @Column(nullable = false, length = 10)
    private String semestre;

    // Date à laquelle la note a été attribuée
    @Column(name = "date_note", nullable = false)
    private LocalDate dateNote;

    // Commentaire optionnel du professeur
    @Column(length = 300)
    private String commentaire;

    // ── Relation @ManyToOne vers Etudiant ─────────────────────────────
    // LECTURE : "plusieurs Notes appartiennent à UN Etudiant"
    // @JoinColumn → colonne "etudiant_id" dans la table "notes"
    // nullable = false → une note DOIT avoir un étudiant
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    // ── Relation @ManyToOne vers Cours ────────────────────────────────
    // LECTURE : "plusieurs Notes concernent UN Cours"
    // @JoinColumn → colonne "cours_id" dans la table "notes"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", nullable = false)
    private Cours cours;

    // @PrePersist — RAPPEL Jour 1 :
    // JPA appelle automatiquement cette méthode avant chaque INSERT
    // Si dateNote n'est pas fournie, on met aujourd'hui
    @PrePersist
    protected void onCreate() {
        if (this.dateNote == null) {
            this.dateNote = LocalDate.now();
        }
    }
}
