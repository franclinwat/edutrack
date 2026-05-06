package com.edutrack.service;

import com.edutrack.dto.request.NoteRequestDTO;
import com.edutrack.dto.response.NoteResponseDTO;
import com.edutrack.exception.CoursNotFoundException;
import com.edutrack.exception.EtudiantNotFoundException;
import com.edutrack.exception.NoteNotFoundException;
import com.edutrack.model.Cours;
import com.edutrack.model.Etudiant;
import com.edutrack.model.Note;
import com.edutrack.repository.CoursRepository;
import com.edutrack.repository.EtudiantRepository;
import com.edutrack.repository.NoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NoteServiceImpl implements INoteService{

    // ── Injection par constructeur ────────────────────────────────────
    // @RequiredArgsConstructor génère le constructeur pour les final
    // SOLID D : on injecte les INTERFACES (JpaRepository)
    // pas les classes concrètes
    private final NoteRepository noteRepository;
    private final EtudiantRepository etudiantRepository;
    private final CoursRepository coursRepository;

    // ════════════════════════════════════════════════════════════════
    // CONVERSIONS PRIVÉES
    // private → SOLID S : cachées du monde extérieur
    // Méthode séparée → si le DTO change, on modifie ici seulement
    // Appelées via this::noteToDto (méthode référence)
    // ════════════════════════════════════════════════════════════════

    // Entité → DTO de réponse
    // On utilise les setters explicites comme dans UserServiceImp4
    // Plus lisible qu'un @Builder avec 10 paramètres pour l'entretien
    private NoteResponseDTO noteToDto(Note note) {
        NoteResponseDTO dto = new NoteResponseDTO();
        dto.setId(note.getId());
        dto.setValeur(note.getValeur());
        dto.setSemestre(note.getSemestre());
        dto.setDateNote(note.getDateNote());
        dto.setCommentaire(note.getCommentaire());

        // Aplatissement des relations @ManyToOne
        // RAPPEL Jour 3 — FetchType.LAZY :
        // On appelle getEtudiant() ici → JPA charge l'Etudiant maintenant
        // C'est intentionnel — on a besoin de ces données pour le DTO
        dto.setEtudiantId(note.getEtudiant().getId());
        dto.setEtudiantNom(note.getEtudiant().getNom());
        dto.setEtudiantPrenom(note.getEtudiant().getPrenom());
        dto.setMatricule(note.getEtudiant().getMatricule());

        dto.setCoursId(note.getCours().getId());
        dto.setCoursTitre(note.getCours().getTitre());
        dto.setCodeCours(note.getCours().getCodeCours());

        // Champ calculé — pas en base de données
        // La règle ">=10 = réussi" est ici dans la couche Service
        // pas dans l'entité Note (SOLID S)
        dto.setEstReussi(note.getValeur() >= 10.0);

        return dto;
    }

    // DTO de requête → Entité
    // On passe etudiant et cours en paramètres car le Service
    // les a déjà chargés depuis la BDD — on évite un double appel
    private Note dtoToNote(NoteRequestDTO dto,
                           Etudiant etudiant, Cours cours) {
        Note note = new Note();
        note.setValeur(dto.getValeur());
        note.setSemestre(dto.getSemestre());
        note.setCommentaire(dto.getCommentaire());

        // On passe les objets complets — JPA a besoin de l'objet
        // pour créer la clé étrangère etudiant_id et cours_id
        note.setEtudiant(etudiant);
        note.setCours(cours);

        // Ce qu'on NE MET PAS ici :
        // id → MySQL le génère (@GeneratedValue)
        // dateNote → @PrePersist la remplit automatiquement
        return note;
    }

    // ════════════════════════════════════════════════════════════════
    // LOGIQUE MÉTIER
    // ════════════════════════════════════════════════════════════════

    @Override
    public NoteResponseDTO ajouterNote(NoteRequestDTO dto) {
        log.info("Ajout note — etudiantId={} coursId={}",
                dto.getEtudiantId(), dto.getCoursId());

        // ── Règle métier 1 : l'étudiant doit exister ─────────────────
        // findById() → Optional<Etudiant>
        // orElseThrow() → si absent, lance EtudiantNotFoundException
        // GlobalExceptionHandler l'attrape → JSON 404 vers Angular
        Etudiant etudiant = etudiantRepository
                .findById(dto.getEtudiantId())
                .orElseThrow(() ->
                        new EtudiantNotFoundException(dto.getEtudiantId()));

        // ── Règle métier 2 : le cours doit exister ───────────────────
        Cours cours = coursRepository
                .findById(dto.getCoursId())
                .orElseThrow(() ->
                        new CoursNotFoundException(dto.getCoursId()));

        // ── Règle métier 3 : pas de doublon ──────────────────────────
        // Un étudiant ne peut pas avoir deux notes dans le même cours
        // IllegalArgumentException → GlobalExceptionHandler → JSON 400
        if (noteRepository.existsByEtudiantIdAndCoursId(
                dto.getEtudiantId(), dto.getCoursId())) {
            log.warn("Doublon note — etudiantId={} coursId={}",
                    dto.getEtudiantId(), dto.getCoursId());
            throw new IllegalArgumentException(
                    "Cet étudiant a déjà une note dans ce cours");
        }

        // Conversion DTO → Entité
        Note note = dtoToNote(dto, etudiant, cours);

        // save() → @PrePersist → INSERT → MySQL génère l'id
        Note sauvegardee = noteRepository.save(note);
        log.info("Note creee — id={} valeur={}",
                sauvegardee.getId(), sauvegardee.getValeur());

        return noteToDto(sauvegardee);
    }

    @Override
    @Transactional(readOnly = true) // optimisation lecture seule
    public List<NoteResponseDTO> getNotesByEtudiant(Long etudiantId) {
        log.info("Lecture notes — etudiantId={}", etudiantId);

        if (!etudiantRepository.existsById(etudiantId)) {
            throw new EtudiantNotFoundException(etudiantId);
        }

        // RAPPEL Module 2 — Stream API :
        // findByEtudiantId() → List<Note>
        // .stream() → pipeline de traitement
        // .map(this::noteToDto) → Function<Note, NoteResponseDTO>
        //    this::noteToDto = méthode référence du Module 2
        // .collect(toList()) → List<NoteResponseDTO>
        return noteRepository.findByEtudiantId(etudiantId)
                .stream()
                .map(this::noteToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDTO> getNotesByEtudiantEtSemestre(
            Long etudiantId, String semestre) {
        log.info("Lecture notes — etudiantId={} semestre={}",
                etudiantId, semestre);

        return noteRepository.findNotesSemestre(etudiantId, semestre)
                .stream()
                .map(this::noteToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculerMoyenne(Long etudiantId, String semestre) {
        log.info("Calcul moyenne — etudiantId={} semestre={}",
                etudiantId, semestre);

        List<Note> notes = noteRepository
                .findNotesSemestre(etudiantId, semestre);

        if (notes.isEmpty()) {
            log.warn("Aucune note — etudiantId={} semestre={}",
                    etudiantId, semestre);
            return 0.0;
        }

        // RAPPEL Module 2 — Stream API :
        // mapToDouble() → DoubleStream optimisé pour les calculs
        // Note::getValeur = méthode référence
        // average() → OptionalDouble
        // orElse(0.0) → si liste vide (ne devrait pas arriver ici)
        return notes.stream()
                .mapToDouble(Note::getValeur)
                .average()
                .orElse(0.0);
    }

    @Override
    public NoteResponseDTO modifierNote(Long id, NoteRequestDTO dto) {
        log.info("Modification note — id={}", id);

        // orElseThrow → NoteNotFoundException → JSON 404
        Note existante = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        // On modifie SEULEMENT valeur et commentaire
        // On ne change pas l'étudiant ni le cours — règle métier
        existante.setValeur(dto.getValeur());
        existante.setCommentaire(dto.getCommentaire());
        // @PreUpdate → updatedAt mis à jour automatiquement par JPA

        log.info("Note modifiee — id={} nouvelle valeur={}", id,
                dto.getValeur());

        return noteToDto(noteRepository.save(existante));
    }


}
