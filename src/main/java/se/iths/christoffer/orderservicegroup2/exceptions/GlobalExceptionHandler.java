package se.iths.christoffer.orderservicegroup2.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Översätter valideringsfel för inkommande anrop till HTTP-fel och JSON-svar.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Returnerar valideringsfel som ett fältnamn-till-felmeddelande-objekt.
     *
     * @param ex undantaget med valideringsfelen
     * @return HTTP 400 med valideringsfel grupperade per fält
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}