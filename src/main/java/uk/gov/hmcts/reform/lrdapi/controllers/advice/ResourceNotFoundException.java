package  uk.gov.hmcts.reform.lrdapi.controllers.advice;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
