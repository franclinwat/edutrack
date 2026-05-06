package com.edutrack.repository;

import com.edutrack.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
// findById(id) → déjà fourni par JpaRepository
    // existsById(id) → déjà fourni par JpaRepository

    // Chercher par matricule — souvent utilisé à l'authentification
    // Optional car le matricule peut ne pas exister
    // RAPPEL Module 2 — Optional : orElseThrow() dans le Service
    Optional<Etudiant> findByMatricule(String matricule);

    // Vérifier l'unicité de l'email avant création
    // Utilisé dans la règle métier du Service
    boolean existsByEmail(String email);


}
