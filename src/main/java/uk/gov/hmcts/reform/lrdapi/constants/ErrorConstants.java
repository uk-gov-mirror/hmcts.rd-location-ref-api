package uk.gov.hmcts.reform.lrdapi.constants;


public enum ErrorConstants {

    MALFORMED_JSON("1 : Malformed Input Request"),

    UNSUPPORTED_MEDIA_TYPES("2 : Unsupported Media Type"),

    INVALID_REQUEST("3 : There is a problem with your request. Please check and try again"),

    EMPTY_RESULT_DATA_ACCESS("4 : Resource not found"),

    UNKNOWN_EXCEPTION("8 : error was caused by an unknown exception"),

    ACCESS_EXCEPTION("9 : Access Denied");

    private final String errorMessage;

    ErrorConstants(String  errorMessage) {
        this.errorMessage  = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
