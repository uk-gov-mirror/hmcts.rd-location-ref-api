package uk.gov.hmcts.reform.lrdapi.controllers.advice;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
