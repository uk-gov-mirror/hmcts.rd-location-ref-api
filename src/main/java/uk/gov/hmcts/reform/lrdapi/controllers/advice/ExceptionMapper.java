package uk.gov.hmcts.reform.lrdapi.controllers.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.ErrorConstants.ACCESS_EXCEPTION;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.ErrorConstants.EMPTY_RESULT_DATA_ACCESS;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.ErrorConstants.INVALID_REQUEST;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.ErrorConstants.MALFORMED_JSON;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.ErrorConstants.UNKNOWN_EXCEPTION;


@Slf4j
@ControllerAdvice(basePackages = "uk.gov.hmcts.reform.lrdapi.controllers")
@RequestMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
public class ExceptionMapper {

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    private static final String HANDLING_EXCEPTION_TEMPLATE = "{}:: handling exception: {}";

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Object> handleEmptyResultDataAccessException(
            EmptyResultDataAccessException ex) {
        return errorDetailsResponseEntity(ex, NOT_FOUND, EMPTY_RESULT_DATA_ACCESS.getErrorMessage());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return errorDetailsResponseEntity(ex, BAD_REQUEST, INVALID_REQUEST.getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return errorDetailsResponseEntity(ex, BAD_REQUEST, INVALID_REQUEST.getErrorMessage());
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<Object> handleHttpStatusException(HttpStatusCodeException ex) {
        HttpStatus httpStatus = ex.getStatusCode();
        return errorDetailsResponseEntity(ex, httpStatus, httpStatus.getReasonPhrase());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> httpMessageNotReadableExceptionError(HttpMessageNotReadableException ex) {
        return errorDetailsResponseEntity(ex, BAD_REQUEST, MALFORMED_JSON.getErrorMessage());

    }

    @ExceptionHandler(LrdApiException.class)
    public ResponseEntity<Object> getUserProfileExceptionError(LrdApiException ex) {
        return errorDetailsResponseEntity(ex, ex.getHttpStatus(), ex.getErrorMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        return errorDetailsResponseEntity(ex, INTERNAL_SERVER_ERROR, UNKNOWN_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
        ResourceNotFoundException ex) {
        return errorDetailsResponseEntity(ex, NOT_FOUND, EMPTY_RESULT_DATA_ACCESS.getErrorMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleForbiddenException(Exception ex) {
        return errorDetailsResponseEntity(ex, FORBIDDEN, ACCESS_EXCEPTION.getErrorMessage());
    }

    private String getTimeStamp() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS", Locale.ENGLISH).format(new Date());
    }

    private static Throwable getRootException(Throwable exception) {
        Throwable rootException = exception;
        while (rootException.getCause() != null) {
            rootException = rootException.getCause();
        }
        return rootException;
    }

    private ResponseEntity<Object> errorDetailsResponseEntity(Exception ex, HttpStatus httpStatus, String errorMsg) {

        log.info(HANDLING_EXCEPTION_TEMPLATE, loggingComponentName, ex.getMessage(), ex);
        ErrorResponse errorDetails = new ErrorResponse(errorMsg, getRootException(ex).getLocalizedMessage(),
                getTimeStamp());

        return new ResponseEntity<>(errorDetails, httpStatus);
    }
}
