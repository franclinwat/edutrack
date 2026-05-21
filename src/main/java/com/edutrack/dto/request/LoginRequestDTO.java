package com.edutrack.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Ce que le client envoie pour se connecter
@Data
@NoArgsConstructor
@AllArgsConstructor

public class LoginRequestDTO {

    @Email(message = "Format email invalide")
    @NotBlank(message = "Email obligatoire")
    private String email;

    @NotBlank(message = "Mot de passe obligatoire")
    private String motDePasse;
}
