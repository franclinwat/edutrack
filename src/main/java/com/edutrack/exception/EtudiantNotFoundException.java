package com.edutrack.exception;

public class EtudiantNotFoundException extends RuntimeException {
    public EtudiantNotFoundException(Long id ) {
        super("Etudiant introuvable : id=" + id);
    }
}
