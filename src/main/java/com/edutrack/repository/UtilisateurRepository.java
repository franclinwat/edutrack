package com.edutrack.repository;

import com.edutrack.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur,Long> {

    // Chercher par email — utilisé par UserDetailsService
    // pour l'authentification
    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);
}
