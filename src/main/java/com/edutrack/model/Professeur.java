package com.edutrack.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "professeurs")
@DiscriminatorValue("PROFESSEUR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Professeur extends Utilisateur{

    // ── Champs spécifiques au professeur ─────────────────────────────
    @NotBlank(message = "La spécialité est obligatoire")
    @Column(nullable = false, length = 100)
    private String specialite;  // "Java", "Bases de données", "Angular"

    // ENUM serait mieux ici mais String = plus simple pour l'entretien
    // Valeurs : "ASSISTANT", "MAITRE_CONF", "PROFESSEUR_TITULAIRE"
    @Column(length = 30)
    private String grade = "ASSISTANT";

    @Override
    public String getRole() {
        // LIEN futur @PreAuthorize :
        // @PreAuthorize("hasRole('PROFESSEUR')") vérifie
        // que getRole() retourne exactement "PROFESSEUR"
        return "PROFESSEUR";
    }
}
