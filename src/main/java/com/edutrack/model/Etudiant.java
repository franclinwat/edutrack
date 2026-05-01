package com.edutrack.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "etudiants")
@DiscriminatorValue("ETUDIANT")
@EqualsAndHashCode(callSuper = true)
// RAPPEL Module 1 : callSuper = true → inclut les champs du parent
// (Utilisateur) dans equals() et hashCode()
// Sans ça, deux Etudiants avec le même id seraient "différents"
public class Etudiant extends Utilisateur {

    // ── Champs spécifiques à l'étudiant ───────────────────────────────
    // Ces colonnes n'existent QUE dans la table "etudiants"
    // Pas dans "utilisateurs"

    @NotBlank(message = "Le matricule est obligatoire")
    @Column(nullable = false, unique = true, length = 20)
    // unique = true → deux étudiants ne peuvent pas avoir le même matricule
    // Ce champ sera INDEXÉ plus tard (Module BDD) car on cherche
    // souvent par matricule
    private String matricule;

    @NotBlank(message = "La filière est obligatoire")
    @Column(nullable = false, length = 50)
    private String filiere;

    // 1, 2, 3... année d'étude
    @NotNull
    @Min(value = 1, message = "L'année doit être au moins 1")
    @Max(value = 5, message = "L'année ne peut pas dépasser 5")
    @Column(name = "annee_etude", nullable = false)
    private Integer anneeEtude;

    // ── Implémentation de la méthode abstraite ────────────────────────
    // RAPPEL polymorphisme :
    // On EST OBLIGÉ d'implémenter getRole() car Utilisateur l'exige
    // @Override signale au compilateur qu'on remplace la méthode parente
    // Si on oublie @Override et qu'on fait une faute de frappe :
    // getRoles() → le compilateur détecte l'erreur immédiatement
    @Override
    public String getRole() {
        return "ETUDIANT";
    }


}
