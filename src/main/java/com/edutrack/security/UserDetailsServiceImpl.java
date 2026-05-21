package com.edutrack.security;

import com.edutrack.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor  // génère constructeur pour les champs final
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    // ❌ Ton code avait : UserRepository userRepository (pas final)
    // → @RequiredArgsConstructor n'injecte que les champs final
    // → userRepository était null → NullPointerException au runtime
    // ✅ Correction : final obligatoire
    private final UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        log.debug("Chargement utilisateur : {}", email);

        // findByEmail → Optional<Utilisateur>
        // orElseThrow → UsernameNotFoundException si pas trouvé
        // Spring Security attrape cette exception → retourne 401
        return utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Utilisateur introuvable : {}", email);
                    return new UsernameNotFoundException(
                            "Utilisateur introuvable : " + email);
                });
    }
}
