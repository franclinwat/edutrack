package com.edutrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="utilisateurs")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_utilisateur")
public abstract  class  Utilisateur implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false, length = 100)
    private String prenom;

    @Email(message = "Format email invalide")
    // @Email vérifie que la valeur respecte le format xxx@xxx.xxx
    @NotBlank(message = "L'email est obligatoire")
    @Column(nullable = false, unique = true)
    // unique = true → MySQL refuse 2 utilisateurs avec le même email
    // C'est une contrainte UNIQUE en SQL
    private String email;

    // RAPPEL encapsulation Module 1 :
    // Le mot de passe est private — personne ne peut le lire directement
    // Spring Security le lit via le getter que @Data génère
    // En production le mot de passe est HASHÉ (BCrypt) — jamais en clair
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Column(nullable = false)
    private Boolean actif = true;

    public abstract String getRole();

    // ── Méthodes UserDetails — requises par Spring Security ───────────

    // getAuthorities() → retourne les rôles de l'utilisateur
    // Spring Security utilise "ROLE_" comme préfixe obligatoire
    // ETUDIANT → ROLE_ETUDIANT
    // PROFESSEUR → ROLE_PROFESSEUR
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRole()));
    }

    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public String getUsername() {
        return email;
    }

    // Les 3 méthodes suivantes contrôlent l'état du compte
    // Pour simplifier on retourne true — à adapter en production
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    // isEnabled() → utilise notre champ actif
    @Override
    public boolean isEnabled() { return actif; }

}
