package com.edutrack.service;

import com.edutrack.model.Note;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class NoteAnalyseService {

    public List<Note>getNotesReussies(List<Note> notes){
        return notes
                .stream()
                .filter(n->n.getValeur()>=10)
                .collect(Collectors.toList());
    }

   public List<Note>getNotesDuSemestre(List<Note> notes,String semestre){

        return notes
                .stream()
                .filter(n->n.getSemestre().equalsIgnoreCase(semestre))
                .collect(Collectors.toList());
    }


    // Extraire toutes les valeurs
    public List<Double> getValeurs(List<Note> notes){
        return notes
                .stream()
                .map(Note::getValeur)
                .collect(Collectors.toList());
    }


    // Extraire les semestres uniques
    public List<String> getSemestresUniques(List<Note> notes){
        return notes
                .stream()
                .map(Note::getSemestre)
                .distinct()
                .collect(Collectors.toList());
    }

    // Notes triées de la meilleure à la moins bonne
    public List<Note> getNotesTries(List<Note> notes) {

        return notes
                .stream()
                .sorted(Comparator.comparingDouble(Note::getValeur).reversed())
                .collect(Collectors.toList());
    }

    // Valeurs des notes réussies, triées décroissantes
    public List<Double> getValeursReussiesTriees(List<Note> notes) {
        return notes
                .stream()
                .filter(n->n.getValeur()>=10)
                .sorted(Comparator.comparingDouble(Note::getValeur).reversed())
                .map(Note::getValeur)
                .collect(Collectors.toList());
    }

    // ── 5. groupingBy() — regrouper par clé ──────────────────────────
    // groupingBy crée une Map : clé → liste d'éléments
    // Très utile pour les rapports

    // Notes regroupées par semestre
    // Résultat : { "2024-S1" → [note1, note2], "2024-S2" → [note3] }
    public Map<String, List<Note>> grouperParSemestre(List<Note> notes) {
        return notes.stream()
                // Collectors.groupingBy(Note::getSemestre)
                // crée la Map automatiquement
                .collect(Collectors.groupingBy(Note::getSemestre));
    }

    // Moyenne par semestre
    // Résultat : { "2024-S1" → 14.5, "2024-S2" → 12.0 }
    public Map<String, Double> moyenneParSemestre(List<Note> notes) {
        return notes.stream()
                .collect(Collectors.groupingBy(
                        Note::getSemestre,
                        // averagingDouble calcule la moyenne automatiquement
                        Collectors.averagingDouble(Note::getValeur)
                ));
    }

    // ── 6. count() et anyMatch() ──────────────────────────────────────
    // Opérations terminales qui retournent un résultat direct

    // Nombre de notes réussies
    public long compterNotesReussies(List<Note> notes) {
        return notes.stream()
                .filter(n -> n.getValeur() >= 10.0)
                // count() = opération terminale → déclenche le pipeline
                .count();
    }

    // Est-ce qu'il y a au moins une note parfaite (20) ?
    public boolean existeNotePaite(List<Note> notes) {
        return notes.stream()
                // anyMatch() = true dès qu'un élément correspond
                // s'arrête au premier match — efficace
                .anyMatch(n -> n.getValeur() == 20.0);
    }

    // Est-ce que TOUTES les notes sont réussies ?
    public boolean toutesReussies(List<Note> notes) {
        return notes.stream()
                // allMatch() = true seulement si TOUS correspondent
                .allMatch(n -> n.getValeur() >= 10.0);
    }

    // ── 7. mapToDouble + average — calcul numérique ───────────────────

    // Calcule la moyenne — déjà dans NoteServiceImpl
    // Ici on explique pourquoi mapToDouble et pas map
    public Double calculerMoyenne(List<Note> notes) {
        if (notes.isEmpty()) return 0.0;

        return notes.stream()
                // map(Note::getValeur) → Stream<Double> (avec boxing/unboxing)
                // mapToDouble(Note::getValeur) → DoubleStream (primitif, plus rapide)
                // Pour les calculs numériques → toujours mapToDouble
                .mapToDouble(Note::getValeur)
                // average() retourne OptionalDouble car la liste peut être vide
                .average()
                // orElse(0.0) → valeur par défaut si liste vide
                .orElse(0.0);
    }

    // ── 8. findFirst() + Optional ─────────────────────────────────────
    // findFirst() retourne Optional<Note>
    // Optional protège contre NullPointerException

    // Trouver la meilleure note d'un étudiant
    public Optional<Note> getMeilleureNote(List<Note> notes) {
        return notes.stream()
                .sorted(Comparator.comparingDouble(Note::getValeur).reversed())
                // findFirst() retourne Optional<Note>
                // Optional = "peut contenir une Note ou être vide"
                .findFirst();
    }

    // Utilisation de Optional dans le Service
    public Double getValeurMeilleureNote(List<Note> notes) {
        return getMeilleureNote(notes)
                // map() sur Optional — si présent, extrait la valeur
                .map(Note::getValeur)
                // orElse → valeur par défaut si Optional vide
                .orElse(0.0);
    }
}
