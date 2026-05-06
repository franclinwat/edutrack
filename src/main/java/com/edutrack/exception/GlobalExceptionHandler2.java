//package com.edutrack.exception;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestControllerAdvice
//@Slf4j
//public class GlobalExceptionHandler2 {
//
//    @ExceptionHandler({
//            NoteNotFoundException.class,
//            EtudiantNotFoundException.class,
//            CoursNotFoundException.class
//    })
//
//    public ResponseEntity<ErrorResponse> handleNotFound(
//            RuntimeException ex) {
//        log.warn("Ressource introuvable : {}", ex.getMessage());
//
//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(new ErrorResponse(
//                        HttpStatus.NOT_FOUND.value(),
//                        ex.getMessage(),
//                        LocalDateTime.now()
//                ));
//
//
//
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ErrorResponse> handleMetier(IllegalArgumentException ex) {
//        log.warn("Erreur metier: {}", ex.getMessage());
//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(new ErrorResponse(
//                        HttpStatus.BAD_REQUEST.value(),
//                        ex.getMessage(),
//                        LocalDateTime.now()
//                ));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
//        log.error("Erreur inattendue : {}", ex.getMessage());
//
//        return ResponseEntity
//                .internalServerError()
//                .body(new ErrorResponse(
//                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                        ex.getMessage(),
//                        LocalDateTime.now()
//                ));
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
//
//        Map<String, String> erreurs = new HashMap<>();
//        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
//            erreurs.put(fe.getField(), fe.getDefaultMessage());
//        }
//
//        log.warn("Validation echouee : {}", erreurs);
//        return ResponseEntity.badRequest().body(erreurs);
//
//
//    }
//
//}
