package com.edutrack.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

// ════════════════════════════════════════════════════════════════════
// @Component plutôt que @Service
// Raison : JwtService est un utilitaire technique
// @Service = logique métier, @Component = utilitaire général
//
// Ce fichier a 3 responsabilités JWT :
// 1. Générer un token après connexion réussie
// 2. Extraire les données d'un token (email, expiration, rôle)
// 3. Valider qu'un token est correct et non expiré
//
// RAPPEL Module 6 — Interface fonctionnelle :
// Function<Claims, T> dans extractClaim
// Claims::getSubject = méthode référence → String
// Claims::getExpiration = méthode référence → Date
// ════════════════════════════════════════════════════════════════════
@Component
@Slf4j
public class JwtService {

    // @Value injecte depuis application.properties
    // jwt.secret=EduTrackSecretKeyPourSignerLesTokens2024
    @Value("${jwt.secret}")
    private String secretKey;

    // jwt.expiration=86400000 (24h en millisecondes)
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // ─── GÉNÉRATION ──────────────────────────────────────────────────
    // Appelé dans AuthService après vérification email + mot de passe
    // UserDetails est l'interface Spring Security
    // → ton entité Utilisateur l'implémente (getUsername, getAuthorities)
    public String generateToken(UserDetails userDetails) {

        // Extraire le rôle depuis les authorities
        // getAuthorities() retourne Collection<GrantedAuthority>
        // RAPPEL Stream API : stream().findFirst().map()
        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(a -> a.getAuthority()) // "ROLE_ETUDIANT"
                .orElse("ROLE_USER");

        log.info("Génération token pour : {} rôle : {}",
                userDetails.getUsername(), role);

        return Jwts.builder()
                // subject = email → identifie l'utilisateur
                .subject(userDetails.getUsername())
                // claim personnalisé → rôle stocké dans le payload
                // nécessaire pour @PreAuthorize au Module 10
                .claim("role", role)
                // issuedAt = date de création du token
                .issuedAt(new Date(System.currentTimeMillis()))
                // expiration = date limite de validité
                // Sans ça → token valide à vie → faille de sécurité
                .expiration(new Date(
                        System.currentTimeMillis() + jwtExpiration))
                // signature HMAC-SHA256 avec notre clé secrète
                .signWith(getSigningKey())
                .compact();
    }

    // ─── VALIDATION ──────────────────────────────────────────────────
    // Appelé dans JwtAuthFilter à chaque requête
    // Vérifie : email correspond + token non expiré + signature valide
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String email = extractEmail(token);
            boolean valid = email.equals(userDetails.getUsername())
                    && !isTokenExpired(token);
            if (!valid) {
                log.warn("Token invalide pour : {}", email);
            }
            return valid;
        } catch (Exception e) {
            // Token malformé, signature incorrecte, etc.
            log.warn("Erreur validation token : {}", e.getMessage());
            return false;
        }
    }

    // ─── EXTRACTION ──────────────────────────────────────────────────

    // Extrait l'email (subject) du token
    public String extractEmail(String token) {
        // Claims::getSubject = méthode référence
        // équivalent à : claims -> claims.getSubject()
        return extractClaim(token, Claims::getSubject);
    }

    // Extrait la date d'expiration
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Vérifie si le token est expiré
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ─── UTILITAIRES INTERNES ─────────────────────────────────────────

    // Méthode générique — RAPPEL Interface fonctionnelle
    // T = type générique (String pour subject, Date pour expiration)
    // Function<Claims, T> = interface fonctionnelle
    //   → reçoit Claims, retourne T
    //   → claimsResolver.apply(claims) appelle la méthode référence
    private <T> T extractClaim(String token,
                               Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Décode et retourne toutes les données du payload
    // Lance JwtException si signature invalide → capturée en amont
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                // API moderne JJWT 0.12+ — ton code était correct
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}