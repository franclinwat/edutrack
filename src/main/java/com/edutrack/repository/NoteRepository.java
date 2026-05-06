package com.edutrack.repository;

import com.edutrack.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface NoteRepository extends JpaRepository<Note, Long> {

    // Spring analyse le NOM de la méthode et génère le SQL
    // findBy + EtudiantId = WHERE etudiant_id = ?
    // Pas besoin d'écrire le SQL — c'est le but de Spring Data JPA

    // SELECT * FROM notes WHERE etudiant_id = ?
    List<Note> findByEtudiantId(Long etudiantId);

    // SELECT * FROM notes WHERE etudiant_id = ? AND semestre = ?
    List<Note> findByEtudiantIdAndSemestre(Long etudiantId,
                                           String semestre);

    // SELECT * FROM notes WHERE cours_id = ?
    List<Note> findByCoursId(Long coursId);

    // SELECT EXISTS(SELECT 1 FROM notes WHERE etudiant_id=? AND cours_id=?)
    // Utilisé dans le Service pour la règle : pas de doublon
    boolean existsByEtudiantIdAndCoursId(Long etudiantId, Long coursId);

    // ── Requête JPQL personnalisée ────────────────────────────────────
    // Quand la méthode par convention de nommage ne suffit pas
    // JPQL ≠ SQL :
    //   SQL  → noms de TABLES  : SELECT * FROM notes WHERE etudiant_id = ?
    //   JPQL → noms de CLASSES : SELECT n FROM Note n WHERE n.etudiant.id = ?
    //
    // Avantage JPQL : indépendant de la BDD (MySQL, PostgreSQL, Oracle)
    // Si on change de BDD → ce code ne change pas
    //
    // @Param("etudiantId") → lie le paramètre Java au :etudiantId dans JPQL
    // ORDER BY n.valeur DESC → meilleures notes en premier
    @Query("SELECT n FROM Note n " +
            "WHERE n.etudiant.id = :etudiantId " +
            "AND n.semestre = :semestre " +
            "ORDER BY n.valeur DESC")
    List<Note> findNotesSemestre(
            @Param("etudiantId") Long etudiantId,
            @Param("semestre") String semestre);

    // Pour les rapports mensuels — toutes les notes d'un semestre
    // Utile pour le calcul de statistiques globales
    @Query("SELECT n FROM Note n WHERE n.semestre = :semestre")
    List<Note> findBySemestre(@Param("semestre") String semestre);

}
