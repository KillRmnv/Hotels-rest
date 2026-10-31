package com.gpsolutions.hotelsrest.exceptions;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;


@RestControllerAdvice
@Slf4j
public final class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String TYPE_PREFIX = "https://hotels-rest.gpsolutions.com/problems/";

    private static final String GENERIC_FAILURE_DETAIL = "The request could not be processed";

    private static final String INVALID_FIELD_DETAIL = "is invalid";

    private static final String MALFORMED_BODY_DETAIL = "The request body could not be read as JSON";

    @ExceptionHandler(DomainException.class)
    ResponseEntity<ProblemDetail> handleDomain(DomainException ex, WebRequest request) {
        return problem(statusOf(ex), ex.typeSuffix(), ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        log.warn("Rejected a malformed request on {}: {}", request.getDescription(false), ex.getMessage());
        return problem(HttpStatus.BAD_REQUEST, "malformed-request", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<ProblemDetail> handleUnexpected(IllegalStateException ex, WebRequest request) {
        log.error("Unexpected failure on {}", request.getDescription(false), ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error", GENERIC_FAILURE_DETAIL, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
        log.warn("Data constraint violation on {}: {}",
                request.getDescription(false), ex.getMostSpecificCause().getMessage());
        return problem(HttpStatus.CONFLICT, "constraint-violation",
                "The request violates a data constraint (e.g. a duplicate value)", request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<FieldViolation> violations = violationsOf(ex);

        ProblemDetail body = problemDetail(HttpStatus.BAD_REQUEST, "validation-failed", "The request body is not valid",
                request);
        body.setProperty("errors", violations);

        return ResponseEntity.badRequest().body(body);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("Could not read the request body on {}: {}", request.getDescription(false), ex.getMessage());

        return ResponseEntity.badRequest()
                .body(problemDetail(HttpStatus.BAD_REQUEST, "malformed-request", MALFORMED_BODY_DETAIL, request));
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex,
                                                                            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<FieldViolation> violations = ex.getParameterValidationResults().stream().flatMap((result) -> {
            String parameter = result.getMethodParameter().getParameterName();
            return result.getResolvableErrors().stream().map((error) -> toViolation(parameter, error));
        }).toList();

        ProblemDetail body = problemDetail(HttpStatus.BAD_REQUEST, "validation-failed", "The request body is not valid",
                request);
        body.setProperty("errors", violations);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<FieldViolation> violations = ex.getConstraintViolations().stream()
                .map((violation) -> new FieldViolation(
                        violation.getPropertyPath().toString(), violation.getMessage()))
                .toList();

        ProblemDetail body = problemDetail(HttpStatus.BAD_REQUEST, "validation-failed", "The request parameter is not valid",
                request);
        body.setProperty("errors", violations);

        return ResponseEntity.badRequest().body(body);
    }

    private static FieldViolation toViolation(String parameter, MessageSourceResolvable error) {
        String message = Optional.ofNullable(error.getDefaultMessage()).orElse(INVALID_FIELD_DETAIL);
        return new FieldViolation(parameter, message);
    }

    private static List<FieldViolation> violationsOf(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getAllErrors().stream().map(GlobalExceptionHandler::toViolation).toList();
    }

    private static FieldViolation toViolation(ObjectError error) {
        String field = (error instanceof FieldError fieldError) ? fieldError.getField() : error.getObjectName();
        String message = Optional.ofNullable(error.getDefaultMessage()).orElse(INVALID_FIELD_DETAIL);
        return new FieldViolation(field, message);
    }

    private HttpStatus statusOf(DomainException ex) {
        return switch (ex) {
            case NotFoundException ignored -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String type, String detail, WebRequest request) {
        return ResponseEntity.status(status).body(problemDetail(status, type, detail, request));
    }

    private static ProblemDetail problemDetail(HttpStatus status, String type, String detail, WebRequest request) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(status, detail);
        body.setType(URI.create(TYPE_PREFIX + type));
        body.setInstance(URI.create(request.getDescription(false).replaceFirst("^uri=", "")));
        return body;
    }

    public record FieldViolation(String field, String message) {
    }

}
