package com.example.ecommerce.exception;

import com.example.ecommerce.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return apiOrRethrow(request, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BusinessException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex, HttpServletRequest request) {
        String message = ex instanceof MethodArgumentNotValidException manv
                ? manv.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).findFirst().orElse("Validation failed")
                : ex.getMessage();
        return apiOrRethrow(request, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnauthorizedException.class, AccessDeniedException.class})
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleForbidden(Exception ex, HttpServletRequest request) {
        return apiOrRethrow(request, ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleConflict(OptimisticLockingFailureException ex, HttpServletRequest request) {
        return apiOrRethrow(request, ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleServerError(Exception ex, HttpServletRequest request) {
        return apiOrRethrow(request, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<Void>> apiOrRethrow(HttpServletRequest request, String message, HttpStatus status) {
        if (request.getRequestURI().startsWith("/api/")) {
            return ResponseEntity.status(status).body(ApiResponse.error(status.value(), message));
        }
        throw new PageFlowException(message, status);
    }

    public static class PageFlowException extends RuntimeException {
        private final HttpStatus status;
        public PageFlowException(String message, HttpStatus status) { super(message); this.status = status; }
        public HttpStatus getStatus() { return status; }
    }
}
