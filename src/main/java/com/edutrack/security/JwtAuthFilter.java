package com.edutrack.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// ════════════════════════════════════════════════════════════════════
// OncePerRequestFilter → s'exécute UNE SEULE fois par requête HTTP
//
// Ce filtre fait exactement 6 étapes à chaque requête :
// 1. Lire le header Authorization: Bearer TOKEN
// 2. Extraire le token (substring après "Bearer ")
// 3. Extraire l'email du token
// 4. Charger l'utilisateur depuis la BDD
// 5. Valider le token
// 6. Enregistrer l'utilisateur dans le SecurityContext
//    → Spring Security sait qui fait la requête
//    → @PreAuthorize peut ensuite vérifier le rôle
// ════════════════════════════════════════════════════════════════════
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // ── Étape 1 : lire le header Authorization ────────────────────
        final String authHeader = request.getHeader("Authorization");

        // Si pas de header ou pas de "Bearer " → pas de token
        // On laisse passer → Spring Security refusera si route protégée
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ── Étape 2 : extraire le token ───────────────────────────────
        // "Bearer eyJhbGci..." → on prend tout après les 7 caractères
        final String jwt = authHeader.substring(7);

        // ── Étape 3 : extraire l'email ────────────────────────────────
        // try/catch : si le token est malformé → on continue sans auth
        final String email;
        try {
            email = jwtService.extractEmail(jwt);
        } catch (Exception e) {
            log.warn("Token malformé : {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // ── Étapes 4-6 : uniquement si email extrait ET pas déjà auth ─
        // SecurityContextHolder.getContext().getAuthentication() == null
        // → vérifie que l'utilisateur n'est pas déjà authentifié
        // → évite de recharger depuis la BDD si déjà fait
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // ── Étape 4 : charger depuis la BDD ──────────────────────
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(email);

            // ── Étape 5 : valider le token ────────────────────────────
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // ── Étape 6 : enregistrer dans SecurityContext ─────────
                // UsernamePasswordAuthenticationToken :
                // - principal = userDetails (l'utilisateur)
                // - credentials = null (le mot de passe n'est plus nécessaire)
                // - authorities = les rôles (ROLE_ETUDIANT, ROLE_PROFESSEUR)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Ajoute les détails HTTP (IP, session ID...)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                // Spring Security sait maintenant qui fait la requête
                // @PreAuthorize pourra vérifier getAuthorities()
                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);

                log.debug("Utilisateur authentifié : {} rôle : {}",
                        email, userDetails.getAuthorities());
            }
        }

        // Passer la requête au filtre suivant (DispatcherServlet)
        filterChain.doFilter(request, response);
    }
}