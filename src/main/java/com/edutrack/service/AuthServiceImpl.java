package com.edutrack.service;

import com.edutrack.dto.request.LoginRequestDTO;
import com.edutrack.dto.response.LoginResponseDTO;
import com.edutrack.model.Utilisateur;
import com.edutrack.repository.UtilisateurRepository;
import com.edutrack.security.JwtService;
import com.edutrack.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtService jwtService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        log.info("Tentative connexion : {}", dto.getEmail());

        // AuthenticationManager vérifie email + mot de passe
        // Utilise DaoAuthenticationProvider + BCrypt
        // Lance BadCredentialsException si invalide → 401
        UserDetails userDetails = (UserDetails) authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getEmail(),
                                dto.getMotDePasse()
                        )
                ).getPrincipal();

        // Charger l'entité complète pour nom et prénom
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(dto.getEmail())
                .orElseThrow();

        // Générer le JWT
        String token = jwtService.generateToken(userDetails);

        log.info("Connexion réussie : {} rôle : {}",
                dto.getEmail(), utilisateur.getRole());

        return LoginResponseDTO.builder()
                .token(token)
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .build();
    }
}