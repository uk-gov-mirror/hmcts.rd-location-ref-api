package uk.gov.hmcts.reform.lrdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import uk.gov.hmcts.reform.lrdapi.controllers.advice.ErrorResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdOrgInfoServiceResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.ErrorConstants.EMPTY_RESULT_DATA_ACCESS;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.ErrorConstants.INVALID_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.EXCEPTION_MSG_SPCL_CHAR;
import static uk.gov.hmcts.reform.lrdapi.util.FeatureConditionEvaluation.FORBIDDEN_EXCEPTION_LD;

class RetrieveOrgServiceDetailsIntegrationTest
    extends LrdAuthorizationEnabledIntegrationTest {

    private static final String HTTP_STATUS = "http_status";
    private static final String RESPONSE_BODY = "response_body";

    private static final String SERVICE_CODE = "AAA6";
    private static final String UNKNOWN_SERVICE_CODE = "A1A7";

    private static final String CCD_CASE_TYPE = "MONEYCLAIMCASE";
    private static final String UNKNOWN_CCD_CASE_TYPE = "ccCaseType1";

    private static final String CCD_SERVICE_NAME = "CMC";
    private static final String SECOND_CCD_SERVICE_NAME = "CCDSERVICENAME2";

    private static final String EXPECTED_BUSINESS_AREA = "Civil, Family and Tribunals";
    private static final String EXPECTED_ORG_UNIT = "HMCTS";
    private static final String EXPECTED_JURISDICTION = "Civil";
    private static final String EXPECTED_SERVICE_DESCRIPTION = "Specified Money Claims";
    private static final String EXPECTED_SUB_BUSINESS_AREA = "Civil and Family";

    private static final int EXPECTED_SERVICE_ID = 3;
    private static final int EXPECTED_CASE_TYPE_COUNT = 2;

    @Test
    void shouldReturnOrgServiceDetailsByServiceCode() throws JsonProcessingException {

        final List<LrdOrgInfoServiceResponse> responses = findOrgServiceDetailsByServiceCode();

        assertThat(responses).hasSize(1);

        assertExpectedOrgServiceDetails(responses);
    }

    @Test
    void shouldReturnNotFoundForUnknownServiceCode()
        throws JsonProcessingException {

        final Object result =
            lrdApiClient.findOrgServiceDetailsByServiceCode(
                UNKNOWN_SERVICE_CODE,
                ErrorResponse.class
            );

        final ErrorResponse errorResponse = assertErrorResponse(result, HttpStatus.NOT_FOUND);

        assertThat(errorResponse.getErrorMessage())
            .isEqualTo(EMPTY_RESULT_DATA_ACCESS.getErrorMessage());
    }

    @Test
    void shouldReturnOrgServiceDetailsByCcdCaseType()
        throws JsonProcessingException {

        final List<LrdOrgInfoServiceResponse> responses = findOrgServiceDetailsByCcdCaseType(CCD_CASE_TYPE);

        assertThat(responses).hasSize(1);

        assertExpectedOrgServiceDetails(responses);
    }

    @Test
    void shouldReturnOrgServiceDetailsByCcdServiceName() throws JsonProcessingException {

        final List<LrdOrgInfoServiceResponse> responses = findOrgServiceDetailsByCcdServiceName(CCD_SERVICE_NAME);

        assertThat(responses).hasSize(1);

        assertExpectedOrgServiceDetails(responses);
    }

    @Test
    void shouldReturnOrgServiceDetailsByMultipleCcdServiceNames() throws JsonProcessingException {

        final List<LrdOrgInfoServiceResponse> responses = findOrgServiceDetailsByCcdServiceName(
            CCD_SERVICE_NAME + "," + SECOND_CCD_SERVICE_NAME
        );

        assertThat(responses).hasSize(2);
    }

    @Test
    void shouldReturnAllOrgServiceDetailsForAllCcdServiceName() throws JsonProcessingException {

        final List<LrdOrgInfoServiceResponse> actualResponses = findOrgServiceDetailsByCcdServiceName("ALL");

        final List<LrdOrgInfoServiceResponse> expectedResponses = findAllOrgServiceDetails();

        assertSameServices(actualResponses, expectedResponses);
    }

    @Test
    void shouldReturnNotFoundForUnknownCcdServiceName() throws JsonProcessingException {

        final Object result = lrdApiClient.findOrgServiceDetailsByCcdServiceName(
            "someRandomServiceName",
            ErrorResponse.class
        );

        final ErrorResponse errorResponse = assertErrorResponse(result, HttpStatus.NOT_FOUND);

        assertThat(errorResponse.getErrorMessage())
            .isEqualTo(EMPTY_RESULT_DATA_ACCESS.getErrorMessage());
    }

    @Test
    void shouldIgnoreCaseAndWhitespaceForCcdCaseType() throws JsonProcessingException {

        final List<LrdOrgInfoServiceResponse> responses = findOrgServiceDetailsByCcdCaseType(" moneyCLAIMCASE ");

        assertThat(responses).hasSize(1);

        assertExpectedOrgServiceDetails(responses);
    }

    @Test
    void shouldReturnNotFoundForUnknownCcdCaseType() throws JsonProcessingException {

        final Object result =
            lrdApiClient.findOrgServiceDetailsByCcdCaseType(
                UNKNOWN_CCD_CASE_TYPE,
                ErrorResponse.class
            );

        final ErrorResponse errorResponse = assertErrorResponse(result, HttpStatus.NOT_FOUND);

        assertThat(errorResponse.getErrorMessage())
            .isEqualTo(EMPTY_RESULT_DATA_ACCESS.getErrorMessage());
    }

    @Test
    void shouldReturnAllOrgServiceDetailsWithoutInputParams() throws JsonProcessingException {

        final List<LrdOrgInfoServiceResponse> actualResponses =
            findAllOrgServiceDetails();

        final List<LrdOrgInfoServiceResponse> expectedResponses =
            findOrgServiceDetailsByCcdServiceName("ALL");

        assertSameServices(actualResponses, expectedResponses);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldReturnForbiddenWhenRetrieveOrgServiceDetailsFlagIsDisabled() throws JsonProcessingException {

        final String featureFlag = "lrd-disable-retrieve-org";

        final Map<String, String> launchDarklyMap = new HashMap<>();
        launchDarklyMap.put(
            "LrdApiController.retrieveOrgServiceDetails",
            featureFlag
        );

        when(featureToggleService.isFlagEnabled(anyString(), anyString()))
            .thenReturn(false);

        when(featureToggleService.getLaunchDarklyMap())
            .thenReturn(launchDarklyMap);

        final Object result =
            lrdApiClient.findOrgServiceDetailsByCcdCaseType(
                UNKNOWN_CCD_CASE_TYPE,
                ErrorResponse.class
            );

        final ErrorResponse errorResponse = assertErrorResponse(result, HttpStatus.FORBIDDEN);

        assertThat(errorResponse.getErrorMessage())
            .contains(featureFlag)
            .contains(FORBIDDEN_EXCEPTION_LD);
    }

    @ParameterizedTest
    @ValueSource(strings = {" aLL ", "All, CMC"})
    void shouldTreatAllAsRequestForAllServices(final String ccdServiceNames) throws JsonProcessingException {

        final List<LrdOrgInfoServiceResponse> actualResponses =
            findOrgServiceDetailsByCcdServiceName(ccdServiceNames);

        final List<LrdOrgInfoServiceResponse> expectedResponses =
            findAllOrgServiceDetails();

        assertSameServices(actualResponses, expectedResponses);
    }

    @Test
    void shouldReturnBadRequestWhenCcdServiceNamesContainEmptyValue() throws JsonProcessingException {

        final Object result =
            lrdApiClient.findOrgServiceDetailsByCcdServiceName(
                "CMC, ,Divorce",
                ErrorResponse.class
            );

        assertInvalidRequestResponse(result);
    }

    @Test
    void shouldReturnBadRequestWhenCcdServiceNamesContainSpecialCharacters() throws JsonProcessingException {

        final Object result =
            lrdApiClient.findOrgServiceDetailsByCcdServiceName(
                ", &",
                ErrorResponse.class
            );

        assertInvalidRequestResponse(result);
    }

    private List<LrdOrgInfoServiceResponse> findOrgServiceDetailsByServiceCode() throws JsonProcessingException {

        final Object result =
            lrdApiClient.findOrgServiceDetailsByServiceCode(
                RetrieveOrgServiceDetailsIntegrationTest.SERVICE_CODE,
                LrdOrgInfoServiceResponse[].class
            );

        return toOrgServiceResponseList(result);
    }

    private List<LrdOrgInfoServiceResponse> findOrgServiceDetailsByCcdCaseType(
        final String ccdCaseType) throws JsonProcessingException {

        final Object result =
            lrdApiClient.findOrgServiceDetailsByCcdCaseType(
                ccdCaseType,
                LrdOrgInfoServiceResponse[].class
            );

        return toOrgServiceResponseList(result);
    }

    private List<LrdOrgInfoServiceResponse> findOrgServiceDetailsByCcdServiceName(
        final String ccdServiceName) throws JsonProcessingException {

        final Object result =
            lrdApiClient.findOrgServiceDetailsByCcdServiceName(
                ccdServiceName,
                LrdOrgInfoServiceResponse[].class
            );

        return toOrgServiceResponseList(result);
    }

    private List<LrdOrgInfoServiceResponse> findAllOrgServiceDetails() throws JsonProcessingException {

        final Object result =
            lrdApiClient.findOrgServiceDetailsByDefaultAll(
                LrdOrgInfoServiceResponse[].class
            );

        return toOrgServiceResponseList(result);
    }

    private List<LrdOrgInfoServiceResponse> toOrgServiceResponseList(final Object result) {

        assertThat(result)
            .as("Expected service response to be a List")
            .isInstanceOf(List.class);

        final List<?> rawResponses = (List<?>) result;

        assertThat(rawResponses)
            .allSatisfy(response ->
                            assertThat(response)
                                .as("Expected list element to be LrdOrgInfoServiceResponse")
                                .isInstanceOf(LrdOrgInfoServiceResponse.class)
            );

        return rawResponses.stream()
            .map(LrdOrgInfoServiceResponse.class::cast)
            .toList();
    }

    private ErrorResponse assertErrorResponse(final Object result, final HttpStatus expectedStatus) {

        assertThat(result)
            .as("Expected error response to be a Map")
            .isInstanceOf(Map.class);

        final Map<?, ?> response = (Map<?, ?>) result;

        assertThat(response.get(HTTP_STATUS))
            .isEqualTo(expectedStatus);

        final Object responseBody = response.get(RESPONSE_BODY);

        assertThat(responseBody)
            .as("Expected response_body to be an ErrorResponse")
            .isInstanceOf(ErrorResponse.class);

        return ErrorResponse.class.cast(responseBody);
    }

    private void assertInvalidRequestResponse(final Object result) {
        final ErrorResponse errorResponse =
            assertErrorResponse(result, HttpStatus.BAD_REQUEST);

        assertThat(errorResponse.getErrorMessage())
            .isEqualTo(INVALID_REQUEST_EXCEPTION.getErrorMessage());

        assertThat(errorResponse.getErrorDescription())
            .isEqualTo(EXCEPTION_MSG_SPCL_CHAR);
    }

    private void assertExpectedOrgServiceDetails(final List<LrdOrgInfoServiceResponse> responses) {

        assertThat(responses)
            .allSatisfy(response -> {
                assertThat(response.getServiceId())
                    .isEqualTo(EXPECTED_SERVICE_ID);

                assertThat(response.getBusinessArea())
                    .isEqualToIgnoringCase(EXPECTED_BUSINESS_AREA);

                assertThat(response.getOrgUnit())
                    .isEqualToIgnoringCase(EXPECTED_ORG_UNIT);

                assertThat(response.getCcdServiceName())
                    .isEqualToIgnoringCase(CCD_SERVICE_NAME);

                assertThat(response.getServiceCode())
                    .isEqualTo(SERVICE_CODE);

                assertThat(response.getJurisdiction())
                    .isEqualToIgnoringCase(EXPECTED_JURISDICTION);

                assertThat(response.getLastUpdate())
                    .isNotNull();

                assertThat(response.getServiceDescription())
                    .isEqualToIgnoringCase(EXPECTED_SERVICE_DESCRIPTION);

                assertThat(response.getServiceShortDescription())
                    .isEqualToIgnoringCase(EXPECTED_SERVICE_DESCRIPTION);

                assertThat(response.getSubBusinessArea())
                    .isEqualToIgnoringCase(EXPECTED_SUB_BUSINESS_AREA);

                assertThat(response.getCcdCaseTypes())
                    .hasSize(EXPECTED_CASE_TYPE_COUNT);
            });
    }

    private void assertSameServices(final List<LrdOrgInfoServiceResponse> actualResponses,
                                    final List<LrdOrgInfoServiceResponse> expectedResponses) {

        assertThat(actualResponses)
            .isNotEmpty()
            .hasSameSizeAs(expectedResponses)
            .extracting(LrdOrgInfoServiceResponse::getServiceCode)
            .containsExactlyInAnyOrderElementsOf(
                expectedResponses.stream()
                    .map(LrdOrgInfoServiceResponse::getServiceCode)
                    .toList()
            );
    }
}

