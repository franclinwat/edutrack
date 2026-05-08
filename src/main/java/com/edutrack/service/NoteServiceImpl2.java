//package com.edutrack.service;
//
//import com.edutrack.dto.request.NoteRequestDTO;
//import com.edutrack.dto.response.NoteResponseDTO;
//import com.edutrack.exception.CoursNotFoundException;
//import com.edutrack.exception.EtudiantNotFoundException;
//import com.edutrack.exception.NoteNotFoundException;
//import com.edutrack.model.Cours;
//import com.edutrack.model.Etudiant;
//import com.edutrack.model.Note;
//import com.edutrack.repository.CoursRepository;
//import com.edutrack.repository.EtudiantRepository;
//import com.edutrack.repository.NoteRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@Transactional
//public class NoteServiceImpl2 implements INoteService {
//
//    private final CoursRepository coursRepository;
//    private final NoteRepository noteRepository;
//    private final EtudiantRepository etudiantRepository;
//
//
//    private NoteResponseDTO noteResponseDTO(Note note) {
//        NoteResponseDTO dto = new NoteResponseDTO();
//
//        dto.setId(note.getId());
//        dto.setValeur(note.getValeur());
//        dto.setSemestre(note.getSemestre());
//        dto.setDateNote(note.getDateNote());
//        dto.setCommentaire(note.getCommentaire());
//
//        dto.setEtudiantId(note.getEtudiant().getId());
//        dto.setEtudiantNom(note.getEtudiant().getNom());
//        dto.setEtudiantPrenom(note.getEtudiant().getPrenom());
//        dto.setMatricule(note.getEtudiant().getMatricule());
//
//        dto.setCoursId(note.getCours().getId());
//        dto.setCoursTitre(note.getCours().getTitre());
//        dto.setCodeCours(note.getCours().getCodeCours());
//
//        dto.setEstReussi(note.getValeur() >= 10.0);
//
//        return dto;
//
//    }
//
//
//    private Note noteRequestDto(NoteRequestDTO dto, Etudiant etudiant, Cours cours) {
//        Note note = new Note();
//
//        note.setValeur(dto.getValeur());
//        note.setSemestre(dto.getSemestre());
//        note.setCommentaire(dto.getCommentaire());
//
//        note.setEtudiant(etudiant);
//        note.setCours(cours);
//
//        return note;
//
//    }
//
//    @Override
//    public NoteResponseDTO ajouterNote(NoteRequestDTO dto) {
//        log.info("Ajout note — etudiantId={} coursId={}", dto.getEtudiantId(), dto.getCoursId());
//
//        Etudiant etudiant = etudiantRepository.findById(dto.getEtudiantId())
//                .orElseThrow(() -> new EtudiantNotFoundException(dto.getEtudiantId()));
//
//        Cours cours = coursRepository
//                .findById(dto.getCoursId())
//                .orElseThrow(() ->
//                        new CoursNotFoundException(dto.getCoursId()));
//
//        if (noteRepository.existsByEtudiantIdAndCoursId(dto.getEtudiantId(), dto.getCoursId())) {
//            log.warn("Doublon note — etudiantId={} coursId={}",
//                    dto.getEtudiantId(), dto.getCoursId());
//            throw new IllegalArgumentException(
//                    "Cet étudiant a déjà une note dans ce cours");
//        }
//
//        Note note = noteRequestDto(dto, etudiant, cours);
//        noteRepository.save(note);
//        log.info("Note creee — id={} valeur={}",
//                note.getId(), note.getValeur());
//        return noteResponseDTO(note);
//
//    }
//
//    @Override
//    @Transactional(readOnly = true) // optimisation lecture seule
//    public List<NoteResponseDTO> getNotesByEtudiant(Long etudiantId) {
//        log.info("Lecture notes — etudiantId={}", etudiantId);
//
//        if (!etudiantRepository.existsById(etudiantId)) {
//            throw new EtudiantNotFoundException(etudiantId);
//        }
//
//
//        return noteRepository.findByEtudiantId(etudiantId)
//                .stream()
//                .map(this::noteResponseDTO)
//                .collect(Collectors.toList());
//
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<NoteResponseDTO> getNotesByEtudiantEtSemestre(
//            Long etudiantId, String semestre) {
//        log.info("Lecture notes — etudiantId={} semestre={}",
//                etudiantId, semestre);
//
//        return noteRepository.findNotesSemestre(etudiantId, semestre)
//                .stream()
//                .map(this::noteResponseDTO)
//                .collect(Collectors.toList());
//    }
//
//
//    @Override
//    @Transactional(readOnly = true)
//    public Double calculerMoyenne(Long etudiantId, String semestre) {
//        log.info("Calcul moyenne — etudiantId={} semestre={}",
//                etudiantId, semestre);
//
//        List<Note> notes = noteRepository
//                .findNotesSemestre(etudiantId, semestre);
//
//        if (notes.isEmpty()) {
//            log.warn("Aucune note — etudiantId={} semestre={}",
//                    etudiantId, semestre);
//            return 0.0;
//        }
//
//        // RAPPEL Module 2 — Stream API :
//        // mapToDouble() → DoubleStream optimisé pour les calculs
//        // Note::getValeur = méthode référence
//        // average() → OptionalDouble
//        // orElse(0.0) → si liste vide (ne devrait pas arriver ici)
//        return notes.stream()
//                .mapToDouble(Note::getValeur)
//                .average()
//                .orElse(0.0);
//    }
//
//    @Override
//    public NoteResponseDTO modifierNote(Long id, NoteRequestDTO dto) {
//        log.info("Modification note — id={}", id);
//
//        // orElseThrow → NoteNotFoundException → JSON 404
//        Note existante = noteRepository.findById(id)
//                .orElseThrow(() -> new NoteNotFoundException(id));
//
//        // On modifie SEULEMENT valeur et commentaire
//        // On ne change pas l'étudiant ni le cours — règle métier
//        existante.setValeur(dto.getValeur());
//        existante.setCommentaire(dto.getCommentaire());
//        // @PreUpdate → updatedAt mis à jour automatiquement par JPA
//
//        log.info("Note modifiee — id={} nouvelle valeur={}", id,
//                dto.getValeur());
//
//        return noteResponseDTO(noteRepository.save(existante));
//    }
//
//}
