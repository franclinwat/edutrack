package com.edutrack.service;

import com.edutrack.dto.request.NoteRequestDTO;
import com.edutrack.dto.response.NoteResponseDTO;

import java.util.List;

public interface INoteService {

    // Ajouter une note
    // Préconditions (vérifiées dans l'impl) :
    //   - l'étudiant doit exister → sinon EtudiantNotFoundException
    //   - le cours doit exister → sinon CoursNotFoundException
    //   - pas de doublon → sinon IllegalArgumentException
    // Sera protégé par @PreAuthorize("hasRole('PROFESSEUR')") — Module Security
    NoteResponseDTO ajouterNote(NoteRequestDTO dto);

    // Toutes les notes d'un étudiant
    // Précondition : l'étudiant doit exister
    List<NoteResponseDTO> getNotesByEtudiant(Long etudiantId);

    // Notes filtrées par étudiant ET semestre
    List<NoteResponseDTO> getNotesByEtudiantEtSemestre(
            Long etudiantId, String semestre);

    // Calcule la moyenne d'un étudiant pour un semestre
    // Utilise Stream API dans l'impl : mapToDouble().average()
    Double calculerMoyenne(Long etudiantId, String semestre);

    // Modifier une note existante
    // Précondition : la note doit exister → sinon NoteNotFoundException
    NoteResponseDTO modifierNote(Long id, NoteRequestDTO dto);
}
