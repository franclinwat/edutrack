package com.edutrack.config;

import com.edutrack.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ════════════════════════════════════════════════════════════════════
// @Configuration → Spring gère cette classe comme bean de config
//
// @EnableWebSecurity → active Spring Security sur l'application
//                      sans ça, Spring Security ne fait rien
//
// @EnableMethodSecurity → active @PreAuthorize sur les méthodes
//                         du Controller et du Service
//                         LIEN Jour 9 : @PreAuthorize("hasRole('PROFESSEUR')")
// ════════════════════════════════════════════════════════════════════
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Le filtre JWT qu'on va créer au Jour 9
    // Il lit le token JWT dans chaque requête
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // ── CSRF désactivé ────────────────────────────────────────
                // RAPPEL : on utilise JWT dans le header Authorization
                // pas de cookies → CSRF inutile et bloquant pour une API REST
                .csrf(AbstractHttpConfigurer::disable)

                // ── Gestion des sessions ──────────────────────────────────
                // STATELESS = Spring ne crée PAS de session HTTP
                // Chaque requête doit apporter son token JWT
                // Si on ne met pas STATELESS → Spring crée des sessions
                // et le JWT ne sert à rien
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ── Règles d'autorisation par route ───────────────────────
                .authorizeHttpRequests(auth -> auth

                        // Routes publiques — pas besoin de token
                        // Ex : se connecter ou créer un compte
                        .requestMatchers("/api/auth/**").permitAll()

                        // Routes ADMIN seulement
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // Toutes les autres routes → token obligatoire
                        // Le rôle précis sera vérifié par @PreAuthorize
                        .anyRequest().authenticated()
                )

                // ── Ajout du filtre JWT ───────────────────────────────────
                // On ajoute notre filtre AVANT le filtre d'authentification
                // par login/password de Spring Security
                // Notre filtre lit le JWT et authentifie l'utilisateur
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // ── BCrypt — hashage des mots de passe ────────────────────────────
    // BCrypt est l'algorithme standard pour hasher les mots de passe
    // On ne stocke JAMAIS un mot de passe en clair en base
    //
    // BCrypt génère un hash différent à chaque fois même pour
    // le même mot de passe — protection contre les rainbow tables
    //
    // Exemple :
    // "monMotDePasse" → "$2a$10$N9qo8uLOickgx..."
    // À chaque appel le hash est différent
    // Mais BCrypt.matches("monMotDePasse", hash) retourne toujours true
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── AuthenticationManager ─────────────────────────────────────────
    // Utilisé par le AuthController pour vérifier email + mot de passe
    // lors de la connexion
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}