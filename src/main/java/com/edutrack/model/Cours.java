package com.edutrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cours")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Column(nullable = false, length = 150)
    private String titre;

    // Code unique du cours — ex: "INFO301", "MATH201"
    // Ce champ sera INDEXÉ au Module BDD
    // Raison : on cherche très souvent un cours par son code
    // Sans index → MySQL lit toute la table
    // Avec index → MySQL va directement à la bonne ligne
    @NotBlank(message = "Le code cours est obligatoire")
    @Column(name = "code_cours", nullable = false, unique = true, length = 20)
    private String codeCours;

    @Column(length = 500)
    private String description;

    // Nombre de crédits du cours (système européen ECTS)
    @NotNull
    @Min(1) @Max(10)
    @Column(name = "nb_credits", nullable = false)
    private Integer nbCredits;

    // ── Relation @ManyToOne ───────────────────────────────────────────
    // LECTURE : "plusieurs Cours appartiennent à UN Professeur"
    //
    // @ManyToOne → JPA crée une colonne "professeur_id" dans la table
    //              "cours" — c'est la clé étrangère
    //              Elle pointe vers l'id dans la table "professeurs"
    //
    // @JoinColumn(name = "professeur_id")
    //   → précise le nom exact de la colonne clé étrangère
    //     Sans @JoinColumn, JPA génère un nom automatique peu lisible
    //
    // fetch = FetchType.LAZY
    //   → JPA ne charge PAS le Professeur automatiquement
    //     quand on charge un Cours
    //   → Il attend qu'on appelle getCours().getProfesseur()
    //   → Bonne pratique : évite de charger des données inutiles
    //   → EAGER = charge tout immédiatement (déconseillé par défaut)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professeur_id", nullable = false)
    private Professeur professeur;

    // ── Relation @OneToMany ───────────────────────────────────────────
    // LECTURE : "UN Cours a PLUSIEURS Notes"
    //
    // mappedBy = "cours"
    //   → dit à JPA : la clé étrangère N'EST PAS ici (dans Cours)
    //     Elle est dans Note, dans le champ qui s'appelle "cours"
    //     Sans mappedBy → JPA créerait une table de jointure inutile
    //
    // cascade = CascadeType.ALL
    //   → quand on supprime un Cours, ses Notes sont aussi supprimées
    //   → quand on sauvegarde un Cours avec ses Notes, tout est sauvegardé
    //
    // orphanRemoval = true
    //   → si on retire une Note de la liste, elle est supprimée en base
    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL,
            orphanRemoval = true)
    // @Builder.Default → nécessaire avec @Builder pour initialiser
    // la liste à vide — sinon @Builder met null et NullPointerException
    @Builder.Default
    private List<Note> notes = new ArrayList<>();

    @Column(nullable = false)
    private Boolean actif = true;
}

