package com.edutrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// ════════════════════════════════════════════════════════════════════
// Ce que le serveur retourne après création ou lecture d'une note
//
// Différence avec l'entité Note.java :
//   Note.java          → objet JPA lié à MySQL, avec @ManyToOne
//   NoteResponseDTO    → objet JSON envoyé au client
//                        contient des données APLATIES
//                        (pas d'objet Etudiant imbriqué — juste le nom)
//
// Pourquoi aplatir ?
// Si on retourne l'entité directement, Jackson (le convertisseur JSON)
// va suivre les @ManyToOne, charger Etudiant, charger Cours,
// puis essayer de charger les relations de Etudiant...
// → boucle infinie ou chargement de toute la base
// ════════════════════════════════════════════════════════════════════
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteResponseDTO {

    private Long id;
    private Double valeur;
    private String semestre;
    private LocalDate dateNote;
    private String commentaire;

    // On aplatit : au lieu de retourner tout l'objet Etudiant,
    // on retourne juste les informations utiles pour l'affichage
    private Long etudiantId;
    private String etudiantNom;     // vient de etudiant.getNom()
    private String etudiantPrenom;  // vient de etudiant.getPrenom()
    private String matricule;       // vient de etudiant.getMatricule()

    // Idem pour Cours
    private Long coursId;
    private String coursTitre;      // vient de cours.getTitre()
    private String codeCours;       // vient de cours.getCodeCours()

    // Champ calculé — pas en base
    // true si valeur >= 10
    private Boolean estReussi;
}
