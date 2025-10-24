package com.example.movie.common;

import com.example.movie.common.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;



import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - Body JSON sai định dạng / sai kiểu dữ liệu
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Bad request (not readable): {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error("Invalid request body or JSON."));
    }

    // 400 - @Valid trên DTO (POST/PUT) – tổng hợp message theo field
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<?>> handleMethodArgNotValid(MethodArgumentNotValidException ex) {
//        String msg = ex.getBindingResult().getFieldErrors()
//                .stream()
//                .map(fe -> fe.getField() + ": " + (fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage()))
//                .collect(Collectors.joining("; "));
//        log.warn("Validation failed: {}", msg);
//        return ResponseEntity.badRequest().body(ApiResponse.error(msg));
//    }

    // 400 - @Validated trên @RequestParam/@PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraint(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        log.warn("Constraint violation: {}", msg);
        return ResponseEntity.badRequest().body(ApiResponse.error(msg));
    }

    // 400 - Lỗi dữ liệu (trùng UNIQUE, FK, NOT NULL…)
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ApiResponse<?>> handleIntegrity(DataIntegrityViolationException ex) {
//        log.warn("Data integrity violation", ex);
//        return ResponseEntity.badRequest().body(
//                ApiResponse.error("Invalid data: maybe duplicate/unique, foreign key, or NOT NULL violated.")
//        );
//    }

    // 400 - Lỗi nghiệp vụ do bạn chủ động throw
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ApiResponse<?>> handleIllegal(IllegalArgumentException ex) {
//        log.warn("Illegal argument: {}", ex.getMessage());
//        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
//    }

    // 403 - Không đủ quyền
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Forbidden"));
    }

    // 404 - Không tìm thấy (ví dụ service throw new EntityNotFoundException)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(EntityNotFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Not found"));
    }

    // 404 - Sai URL (nếu bạn bật no-handler-found)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoHandler(NoHandlerFoundException ex) {
        log.warn("No handler: {}", ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Endpoint does not exist"));
    }

    // 500 - Bắt mọi thứ còn lại
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleOther(Exception ex) {
        log.error("Internal error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Dữ liệu không hợp lệ",
                "errors", errors,
                "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArg(IllegalArgumentException ex) {
        // Lỗi trùng username/email do service tự kiểm tra → 409
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "success", false,
                "message", ex.getMessage(),
                "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDupIndex(DataIntegrityViolationException ex) {
        String root = Optional.ofNullable(org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage(ex))
                .orElse("").toLowerCase();
        String msg = "Dữ liệu vi phạm ràng buộc";
        if (root.contains("users") && root.contains("username")) msg = "Tên đăng nhập đã tồn tại";
        if (root.contains("users") && root.contains("email"))    msg = "Email đã tồn tại";

        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "success", false,
                "message", msg,
                "timestamp", Instant.now().toString()
        ));
    }
}
