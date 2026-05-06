package com.edutrack.repository;

import com.edutrack.model.Cours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoursRepository extends JpaRepository<Cours, Long> {
    // Chercher par code cours (INFO301, MATH201...)
    // Ce champ sera INDEXÉ au Module BDD pour accélérer cette requête
    Optional<Cours> findByCodeCours(String codeCours);

    // Vérifier l'unicité du code avant création
    boolean existsByCodeCours(String codeCours);
}
