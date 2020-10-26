package uk.gov.hmcts.reform.lrdapi.controllers.advice;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class LrdApiExceptionTest {

    @Test
    public void testLrdApiException() {
        LrdApiException externalApiException = new LrdApiException(BAD_REQUEST, "BAD REQUEST");
        assertThat(externalApiException).isNotNull();
        assertThat(externalApiException.getHttpStatus()).hasToString("400 BAD_REQUEST");
        assertThat(externalApiException.getErrorMessage()).isEqualTo("BAD REQUEST");
    }
}
