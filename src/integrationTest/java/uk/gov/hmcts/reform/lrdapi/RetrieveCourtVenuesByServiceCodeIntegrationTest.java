package uk.gov.hmcts.reform.lrdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.lrdapi.controllers.advice.ErrorResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdCourtVenueResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdCourtVenuesByServiceCodeResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.ErrorConstants.EMPTY_RESULT_DATA_ACCESS;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.ErrorConstants.INVALID_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.EXCEPTION_MSG_SERVICE_CODE_SPCL_CHAR;
import static uk.gov.hmcts.reform.lrdapi.util.FeatureConditionEvaluation.FORBIDDEN_EXCEPTION_LD;

@WithTags({@WithTag("testType:Integration")})
@SuppressWarnings("unchecked")
class RetrieveCourtVenuesByServiceCodeIntegrationTest extends LrdAuthorizationEnabledIntegrationTest {

    public static final String HTTP_STATUS = "http_status";

    @ParameterizedTest
    @ValueSource(strings = {"AAA3","aaa3"})
    void returnsCourtVenuesByServiceCodeWithStatusCode_200(String serviceCode)
        throws JsonProcessingException, JSONException {

        final var response = (LrdCourtVenuesByServiceCodeResponse)
            lrdApiClient.findCourtVenuesByServiceCode(serviceCode, LrdCourtVenuesByServiceCodeResponse.class);

        assertThat(response).isNotNull();
        responseVerification(response);
    }

    @Test
    void returnsCourtVenuesByUnknownServiceCodeWithStatusCode_404() throws JsonProcessingException {

        Map<String, Object> errorResponseMap = (Map<String, Object>)
            lrdApiClient.findCourtVenuesByServiceCode("ABCD1", ErrorResponse.class);

        assertNotNull(errorResponseMap);
        assertThat(errorResponseMap).containsEntry(HTTP_STATUS, HttpStatus.NOT_FOUND);
        ErrorResponse errorResponse = (ErrorResponse) errorResponseMap.get("response_body");
        assertEquals(EMPTY_RESULT_DATA_ACCESS.getErrorMessage(), errorResponse.getErrorMessage());
        assertEquals("No court venues found for the given service code ABCD1",
                     errorResponse.getErrorDescription());
    }

    @Test
    void returnsCourtVenuesByInvalidServiceCodeWithStatusCode_400() throws
        JsonProcessingException {

        Map<String, Object> errorResponseMap = (Map<String, Object>)
            lrdApiClient.findCourtVenuesByServiceCode("@$ABC", ErrorResponse.class);

        assertNotNull(errorResponseMap);
        assertThat(errorResponseMap).containsEntry(HTTP_STATUS, HttpStatus.BAD_REQUEST);
        ErrorResponse errorResponse = (ErrorResponse) errorResponseMap.get("response_body");
        assertEquals(INVALID_REQUEST_EXCEPTION.getErrorMessage(), errorResponse.getErrorMessage());
        assertEquals(EXCEPTION_MSG_SERVICE_CODE_SPCL_CHAR, errorResponse.getErrorDescription());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void returnsCourtVenuesByBlankServiceCodeWithStatusCode_400(String serviceCode) throws
        JsonProcessingException {

        Map<String, Object> errorResponseMap = (Map<String, Object>)
            lrdApiClient.findCourtVenuesByServiceCode(serviceCode, ErrorResponse.class);

        assertNotNull(errorResponseMap);
        assertThat(errorResponseMap).containsEntry(HTTP_STATUS, HttpStatus.BAD_REQUEST);
    }

    @Test
    void returnsCourtVenuesByServiceCode_LdFlagOff_WithStatusCode_403()
        throws Exception {
        Map<String, String> launchDarklyMap = new HashMap<>();
        launchDarklyMap.put(
            "LrdCourtVenueController.retrieveCourtVenuesByServiceCode",
            "lrd_location_api"
        );
        when(featureToggleService.isFlagEnabled(anyString(), anyString())).thenReturn(false);
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        Map<String, Object> errorResponseMap = (Map<String, Object>)
            lrdApiClient.findCourtVenuesByServiceCode("AAA3", ErrorResponse.class);
        assertThat(errorResponseMap).containsEntry("http_status", HttpStatus.FORBIDDEN);
        assertThat(((ErrorResponse) errorResponseMap.get("response_body")).getErrorMessage())
            .contains("lrd_location_api".concat(" ").concat(FORBIDDEN_EXCEPTION_LD));
    }

    private void responseVerification(LrdCourtVenuesByServiceCodeResponse response)
        throws JSONException, JsonProcessingException {
        List<LrdCourtVenueResponse> expectedCourtVenueResponses = buildCourtVenueResponses();

        assertThat(response.getServiceCode()).isEqualTo("AAA3");
        assertThat(response.getCourtTypeId()).isEqualTo("17");
        assertThat(response.getCourtType()).isEqualTo("Employment Tribunal");
        assertThat(response.getWelshCourtType()).isNull();
        ObjectMapper objectMapper = new ObjectMapper();
        String actual = objectMapper.writeValueAsString(response.getCourtVenues());
        String expected = objectMapper.writeValueAsString(expectedCourtVenueResponses);
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    private List<LrdCourtVenueResponse> buildCourtVenueResponses() {
        List<LrdCourtVenueResponse> expectedCourtVenueResponses = new ArrayList<>();
        LrdCourtVenueResponse response1 = LrdCourtVenueResponse.builder()
            .courtVenueId("2")
            .siteName("Aberdeen Tribunal Hearing Centre 2")
            .courtName("ABERDEEN TRIBUNAL HEARING CENTRE 2")
            .epimmsId("123456788")
            .openForPublic("YES")
            .courtTypeId("17")
            .courtType("Employment Tribunal")
            .regionId("1")
            .region("London")
            .clusterId("1")
            .clusterName("Avon, Somerset and Gloucestershire")
            .courtStatus("Open")
            .courtStatusCode("OPEN")
            .postcode("AB11 7KT")
            .courtAddress("AB2, 49 HUNTLY STREET, ABERDEEN")
            .serviceCode("AAA3")
            .build();

        LrdCourtVenueResponse response2 = LrdCourtVenueResponse.builder()
            .courtVenueId("6")
            .siteName("Aberdeen Tribunal Hearing Centre 6")
            .courtName("ABERDEEN TRIBUNAL HEARING CENTRE 6")
            .epimmsId("epimmsId1236")
            .openForPublic("YES")
            .courtTypeId("17")
            .courtType("Employment Tribunal")
            .regionId("2")
            .region("Midlands")
            .courtStatus("Open")
            .courtStatusCode("OPEN")
            .postcode("AB11 7GQ")
            .courtAddress("AB6, 53 HUNTLY STREET, ABERDEEN")
            .serviceCode("AAA3")
            .build();

        LrdCourtVenueResponse response3 = LrdCourtVenueResponse.builder()
            .courtVenueId("10")
            .siteName("Aberdeen Tribunal Hearing Centre 10")
            .courtName("ABERDEEN TRIBUNAL HEARING CENTRE 10")
            .epimmsId("123458")
            .openForPublic("YES")
            .courtTypeId("17")
            .courtType("Employment Tribunal")
            .regionId("1")
            .region("London")
            .clusterId("2")
            .clusterName("Bedfordshire, Cambridgeshire, Hertfordshire")
            .courtStatus("Open")
            .courtStatusCode("OPEN")
            .postcode("AB11 5QA")
            .courtAddress("AB10, 57 HUNTLY STREET, ABERDEEN")
            .isCaseManagementLocation("Y")
            .isHearingLocation("Y")
            .locationType("CTSC")
            .isTemporaryLocation("Y")
            .serviceCode("AAA3")
            .build();

        LrdCourtVenueResponse response4 = LrdCourtVenueResponse.builder()
            .courtVenueId("14")
            .siteName("Aberdeen Tribunal Hearing Centre 7")
            .courtName("ABERDEEN TRIBUNAL HEARING CENTRE 22")
            .epimmsId("123462")
            .openForPublic("YES")
            .courtTypeId("17")
            .courtType("Employment Tribunal")
            .regionId("3")
            .region("North East")
            .clusterId("1")
            .clusterName("Avon, Somerset and Gloucestershire")
            .courtStatus("Open")
            .courtStatusCode("OPEN")
            .postcode("AB11 1TY")
            .courtAddress("AB7, 54 HUNTLY STREET, ABERDEEN")
            .serviceCode("AAA3")
            .externalShortName("Aberdeen Tribunal External")
            .welshExternalShortName("Welsh External Short Name")
            .build();

        expectedCourtVenueResponses.add(response1);
        expectedCourtVenueResponses.add(response2);
        expectedCourtVenueResponses.add(response3);
        expectedCourtVenueResponses.add(response4);

        return expectedCourtVenueResponses;
    }

}
