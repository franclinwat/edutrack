package com.edutrack.config;
import com.edutrack.security.JwtAuthFilter;
import com.edutrack.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ════════════════════════════════════════════════════════════════════
// @Configuration    → classe de configuration Spring
// @EnableWebSecurity → active Spring Security
//
// @EnableMethodSecurity → active @PreAuthorize sur les méthodes
//   CRUCIAL pour le Jour 10 :
//   @PreAuthorize("hasRole('PROFESSEUR')") sur les Controllers
//   Sans cette annotation → @PreAuthorize n'a aucun effet
//
// Ton code n'avait pas @EnableMethodSecurity
// → les @PreAuthorize du prochain module n'auraient pas fonctionné
// ════════════════════════════════════════════════════════════════════
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // ← nécessaire pour @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // ── CSRF désactivé ────────────────────────────────────────
                // RAPPEL Jour 8 :
                // CSRF protège les apps avec cookies de session
                // Notre API REST utilise JWT dans le header Authorization
                // Un site malveillant ne peut pas voler un JWT dans un header
                // → CSRF inutile et bloquant pour notre API REST
                .csrf(AbstractHttpConfigurer::disable)

                // ── Règles d'autorisation par route ───────────────────────
                .authorizeHttpRequests(auth -> auth

                        // Routes publiques — pas besoin de token
                        .requestMatchers("/api/auth/**").permitAll()

                        // Routes ADMIN exclusivement
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Toutes les autres routes → token JWT obligatoire
                        // Le rôle précis est vérifié par @PreAuthorize
                        .anyRequest().authenticated()
                )

                // ── Pas de session HTTP ───────────────────────────────────
                // STATELESS = Spring ne crée et ne stocke aucune session
                // Chaque requête apporte son token JWT
                // Scalable : n'importe quel serveur peut traiter n'importe quelle requête
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ── DaoAuthenticationProvider ─────────────────────────────
                // Ton code avait ça — c'est la bonne pratique
                // DaoAuthenticationProvider connecte :
                //   UserDetailsService (charge l'utilisateur depuis BDD)
                //   PasswordEncoder (vérifie le mot de passe BCrypt)
                .authenticationProvider(authenticationProvider())

                // ── Filtre JWT avant le filtre Spring par défaut ──────────
                // Notre filtre lit le JWT et authentifie l'utilisateur
                // AVANT que Spring essaie d'autres méthodes d'authentification
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── DaoAuthenticationProvider ─────────────────────────────────────
    // Orchestre la vérification email + mot de passe lors du login
    // Connecte UserDetailsService + BCrypt
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Comment charger l'utilisateur depuis la BDD
        provider.setUserDetailsService(userDetailsService);
        // Comment vérifier le mot de passe (BCrypt)
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ── BCrypt ────────────────────────────────────────────────────────
    // Standard industrie pour hasher les mots de passe
    // Jamais MD5, jamais SHA1, jamais en clair
    // BCrypt intègre un salt automatique
    // → même mot de passe = hash différent à chaque fois
    // → BCrypt.matches("mdp", hash) retourne true si correct
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── AuthenticationManager ─────────────────────────────────────────
    // Orchestrateur de l'authentification
    // Injecté dans AuthService pour vérifier email + mot de passe
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}