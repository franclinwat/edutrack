package com.edutrack.exception;

public class CoursNotFoundException extends RuntimeException {

    public CoursNotFoundException(Long id) {
        super("Cours  introuvable : id=" + id);
    }
}
