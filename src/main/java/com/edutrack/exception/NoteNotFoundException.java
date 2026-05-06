package com.edutrack.exception;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(Long id) {
        super("Note introuvable : id=" + id);
    }
}
