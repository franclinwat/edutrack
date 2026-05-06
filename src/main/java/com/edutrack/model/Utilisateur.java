package com.edutrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="utilisateurs")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_utilisateur")
public abstract  class Utilisateur {

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
}
