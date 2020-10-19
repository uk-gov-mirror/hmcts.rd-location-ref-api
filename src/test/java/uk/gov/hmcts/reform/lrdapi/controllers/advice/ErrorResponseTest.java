package uk.gov.hmcts.reform.lrdapi.controllers.advice;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorResponseTest {

    @Test
    public void test_ErrorResponse() {
        String expectMsg = "msg";
        String expectDesc = "desc";
        String expectTs = "time";

        ErrorResponse errorDetails = ErrorResponse.builder()
                .errorDescription("desc")
                .errorMessage(expectMsg)
                .timeStamp("time")
                .build();

        assertThat(errorDetails).isNotNull();
        assertThat(errorDetails.getErrorMessage()).isEqualTo(expectMsg);
        assertThat(errorDetails.getTimeStamp()).isEqualTo(expectTs);
        assertThat(errorDetails.getErrorDescription()).isEqualTo(expectDesc);
    }

    @Test
    public void test_NoArgsConstructor() {
        ErrorResponse errorResponse = new ErrorResponse();
        assertThat(errorResponse).isNotNull();
    }

}
