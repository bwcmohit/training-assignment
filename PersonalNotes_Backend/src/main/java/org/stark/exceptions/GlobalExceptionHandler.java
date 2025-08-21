package org.stark.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler{

  // Handle AES / Crypto errors
  @ExceptionHandler(CryptoException.class)
  public ResponseEntity<Map<String, String>> handleCryptoException(CryptoException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Encryption/Decryption failed");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  // Handle JWT errors
  @ExceptionHandler(JwtException.class)
  public ResponseEntity<Map<String, String>> handleJwtException(JwtException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Invalid or expired token");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  // Handle custom authentication errors
  @ExceptionHandler(AuthException.class)
  public ResponseEntity<Map<String, String>> handleAuthException(AuthException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Authentication Failed");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  // Handle validation errors (@Valid)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return ResponseEntity.badRequest().body(errors);
  }

  // Handle Access Denied (Spring Security)
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Forbidden");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  // Generic handler for IllegalArgumentException, etc.
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArg(IllegalArgumentException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Bad Request");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  // Fallback for any unhandled exceptions
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleAll(Exception ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Internal Server Error");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
