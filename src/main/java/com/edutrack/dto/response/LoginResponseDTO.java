package com.edutrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String token;    // JWT à utiliser dans toutes les requêtes
    private String email;
    private String role;     // "ROLE_ETUDIANT", "ROLE_PROFESSEUR", "ROLE_ADMIN"
    private String nom;
    private String prenom;
}
