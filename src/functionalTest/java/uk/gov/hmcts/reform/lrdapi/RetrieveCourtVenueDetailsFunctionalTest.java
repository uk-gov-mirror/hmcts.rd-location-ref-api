package uk.gov.hmcts.reform.lrdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.lib.util.serenity5.SerenityTest;
import uk.gov.hmcts.reform.lrdapi.controllers.advice.ErrorResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdBuildingLocationResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdCourtVenueResponse;
import uk.gov.hmcts.reform.lrdapi.util.FeatureToggleConditionExtension;
import uk.gov.hmcts.reform.lrdapi.util.ToggleEnable;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.ErrorConstants.EMPTY_RESULT_DATA_ACCESS;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.NO_COURT_VENUES_FOUND_FOR_CLUSTER_ID;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.NO_COURT_VENUES_FOUND_FOR_COURT_TYPE_ID;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.NO_COURT_VENUES_FOUND_FOR_COURT_VENUE_NAME;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.NO_COURT_VENUES_FOUND_FOR_FOR_EPIMMS_ID;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.NO_COURT_VENUES_FOUND_FOR_REGION_ID;
import static uk.gov.hmcts.reform.lrdapi.util.FeatureToggleConditionExtension.getToggledOffMessage;

@SerenityTest
@SpringBootTest
@WithTags({@WithTag("testType:Functional")})
@ActiveProfiles("functional")
class RetrieveCourtVenueDetailsFunctionalTest extends AuthorizationFunctionalTest {

    private static final String mapKey = "LrdCourtVenueController.retrieveCourtVenues";
    private static final String path = "/court-venues";

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldRetrieveCourtVenues_For_A_Epimms_Id_WithStatusCode_200() {
        final var response = (LrdCourtVenueResponse[])
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.OK, "?epimms_id=815833",
                                                         LrdCourtVenueResponse[].class,
                                                         path
            );
        assertThat(response).isNotEmpty();
        boolean isEachIdMatched = Arrays
            .stream(response)
            .map(LrdCourtVenueResponse::getEpimmsId)
            .allMatch("815833"::equals);
        assertTrue(isEachIdMatched);
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldRetrieveCourtVenues_For_Multiple_Epimms_Id_WithStatusCode_200() {
        final var response = (LrdCourtVenueResponse[])
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.OK, "?epimms_id=815833,219164",
                                                                    LrdCourtVenueResponse[].class, path);
        assertThat(response).isNotEmpty();
        boolean expected = Arrays
            .stream(response)
            .map(LrdCourtVenueResponse::getEpimmsId)
            .allMatch("815833,219164"::contains);
        assertTrue(expected);
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldRetrieveCourtVenues_For_No_Epimms_Id_WithStatusCode_200() {
        final var response = (LrdCourtVenueResponse[])
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.OK, null,
                                                                    LrdCourtVenueResponse[].class, path);
        assertThat(response).isNotEmpty();
        boolean isOpenCourts = Arrays
            .stream(response)
            .allMatch(venue -> venue.getCourtStatus().equals("Open"));
        assertTrue(isOpenCourts);
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldRetrieveCourtVenues_For_One_Epimms_Id_And_All_WithStatusCode_200() throws JsonProcessingException {
        final var response = (LrdCourtVenueResponse[])
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.OK, "?epimms_id=All,123456",
                                                                    LrdCourtVenueResponse[].class, path);
        assertThat(response).isNotEmpty();
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldRetrieveCourtVenues_For_All_Epimms_Id_WithStatusCode_200() throws JsonProcessingException {
        final var response = (LrdCourtVenueResponse[])
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.OK, "?epimms_id=All",
                                                         LrdCourtVenueResponse[].class, path);
        assertThat(response).isNotEmpty();
    }


    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldRetrieveCourtVenues_For_A_Epimms_Id_With_CourtType_StatusCode_200() {

        Response response = lrdApiClient.getMultipleAuthHeaders()
            .get("/refdata/location" + path + "?epimms_id=219164&court_type_id=31")
            .andReturn();
        if (response.getStatusCode() == HttpStatus.OK.value()) {

            final var lrdCourtVenueResponse = response.getBody()
                .as(LrdCourtVenueResponse[].class);

            if (lrdCourtVenueResponse.length > 0) {
                assertThat(lrdCourtVenueResponse).isNotEmpty();
                boolean isEachIdMatched = Arrays
                    .stream(lrdCourtVenueResponse)
                    .map(LrdCourtVenueResponse::getEpimmsId)
                    .allMatch("219164"::equals);
                assertTrue(isEachIdMatched);
                boolean isEachCourtTypeMatched = Arrays
                    .stream(lrdCourtVenueResponse)
                    .map(LrdCourtVenueResponse::getCourtTypeId)
                    .allMatch("31"::equals);
                assertTrue(isEachCourtTypeMatched);
            }
        }
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldRetrieveCourtVenues_For_EpimmsId_And_ServiceCode_StatusCode_200() {
        Response response = lrdApiClient.getMultipleAuthHeaders()
            .get("/refdata/location" + path + "?epimms_id=219164&service_code=BFA1")
            .andReturn();

        int statusCode = response.getStatusCode();
        assertTrue(statusCode == HttpStatus.OK.value() || statusCode == HttpStatus.NOT_FOUND.value());

        if (statusCode == HttpStatus.OK.value()) {
            final var lrdCourtVenueResponse = response.getBody()
                .as(LrdCourtVenueResponse[].class);

            if (lrdCourtVenueResponse.length > 0) {
                assertThat(lrdCourtVenueResponse).isNotEmpty();
                boolean isEachIdMatched = Arrays
                    .stream(lrdCourtVenueResponse)
                    .map(LrdCourtVenueResponse::getEpimmsId)
                    .allMatch("219164"::equals);
                assertTrue(isEachIdMatched);
                boolean isEachServiceCodeMatched = Arrays
                    .stream(lrdCourtVenueResponse)
                    .map(LrdCourtVenueResponse::getServiceCode)
                    .allMatch(code -> "BFA1".equalsIgnoreCase(code));
                assertTrue(isEachServiceCodeMatched);
            }
        }

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertEquals(EMPTY_RESULT_DATA_ACCESS.getErrorMessage(), errorResponse.getErrorMessage());
            assertEquals(String.format(NO_COURT_VENUES_FOUND_FOR_FOR_EPIMMS_ID, "[219164]"),
                         errorResponse.getErrorDescription());
        }
    }


    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldRetrieveCourtVenues_For_Given_Region_Id_WithStatusCode_200() throws JsonProcessingException {
        final var response = (LrdCourtVenueResponse[])
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.OK, "?region_id=7",
                                                                    LrdCourtVenueResponse[].class, path);
        assertThat(response).isNotEmpty();
        boolean isExpectedMatch = Arrays
            .stream(response)
            .allMatch(venue -> venue.getRegionId().equals("7") && venue.getCourtStatus().equals("Open"));
        assertTrue(isExpectedMatch);
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldRetrieveCourtVenues_For_Given_CourtType_Id_WithStatusCode_200() throws JsonProcessingException {
        final var response = (LrdCourtVenueResponse[])
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.OK, "?court_type_id=23",
                                                         LrdCourtVenueResponse[].class, path);
        assertThat(response).isNotEmpty();
        boolean isEveryIdMatched = Arrays
            .stream(response)
            .allMatch(venue -> venue.getCourtTypeId().equals("23") && venue.getCourtStatus().equals("Open"));
        assertTrue(isEveryIdMatched);
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldRetrieveCourtVenues_For_ServiceCode_And_CourtType_Without_Epimms_WithStatusCode_200() {
        final var response = (LrdCourtVenueResponse[])
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.OK, "?service_code=AAA6&court_type_id=999",
                                                         LrdCourtVenueResponse[].class, path);

        assertThat(response).isNotEmpty();
        boolean isEveryServiceCodeMatched = Arrays
            .stream(response)
            .allMatch(venue -> "AAA6".equalsIgnoreCase(venue.getServiceCode()));
        assertTrue(isEveryServiceCodeMatched);
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldRetrieveCourtVenues_For_Given_Cluster_Id_WithStatusCode_200() throws JsonProcessingException {
        final var response = (LrdCourtVenueResponse[])
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.OK, "?cluster_id=8",
                                                         LrdCourtVenueResponse[].class, path);
        assertThat(response).isNotEmpty();
        boolean isEveryIdMatched = Arrays
            .stream(response)
            .allMatch(venue -> venue.getClusterId().equals("8") && venue.getCourtStatus().equals("Open"));
        assertTrue(isEveryIdMatched);
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldRetrieveCourtVenues_CourtVenueNameGiven_WithStatusCode_200() throws JsonProcessingException {
        final var response = (LrdCourtVenueResponse[]) lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.OK,
                                                         "?court_venue_name=Aberdeen Tribunal Hearing Centre",
                                                         LrdCourtVenueResponse[].class, path);
        assertThat(response).isNotEmpty();
        boolean isExpectedMatch = Arrays
            .stream(response)
            .allMatch(venue -> venue.getCourtName().equalsIgnoreCase("Aberdeen Tribunal Hearing Centre")
            || venue.getSiteName().equalsIgnoreCase("Aberdeen Tribunal Hearing Centre"));
        assertTrue(isExpectedMatch);
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldRetrieveCourtVenues_WhenParameterEpmIdsValueAllWithYPassed_WithStatusCode_200() throws
        JsonProcessingException {

        final var response = (LrdCourtVenueResponse[]) lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.OK,
                "?epimms_id=ALL"
                    + "&is_hearing_location=N&is_case_management_location=Y&location_type=CTSC"
                    + "&is_temporary_location=N",
                LrdCourtVenueResponse[].class,
                path
            );

        assertThat(response).isNotEmpty();
        assertTrue(Arrays.stream(response).allMatch(venueResponse ->
             venueResponse.getIsHearingLocation().equalsIgnoreCase("N")
             && venueResponse.getIsCaseManagementLocation().equalsIgnoreCase("Y")
             && venueResponse.getLocationType().equalsIgnoreCase("CTSC")
             && venueResponse.getIsTemporaryLocation().equalsIgnoreCase("N")));
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldReturn404_WhenNoEpimmsIdFound() {
        final var response = (ErrorResponse)
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.NOT_FOUND, "?epimms_id=000000",
                                                                    LrdCourtVenueResponse[].class, path);
        assertNotNull(response);
        assertEquals(EMPTY_RESULT_DATA_ACCESS.getErrorMessage(), response.getErrorMessage());
        assertEquals(String.format(NO_COURT_VENUES_FOUND_FOR_FOR_EPIMMS_ID, "[000000]"),
                     response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldReturn404_WhenNoRegionIdFound() {
        final var response = (ErrorResponse)
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.NOT_FOUND, "?region_id=0",
                                                         LrdCourtVenueResponse[].class, path);
        assertNotNull(response);
        assertEquals(EMPTY_RESULT_DATA_ACCESS.getErrorMessage(), response.getErrorMessage());
        assertEquals(String.format(NO_COURT_VENUES_FOUND_FOR_REGION_ID, "0"),
                     response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldReturn404_WhenNoClusterIdFound() {
        final var response = (ErrorResponse)
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.NOT_FOUND, "?cluster_id=0",
                                                         LrdCourtVenueResponse[].class, path);
        assertNotNull(response);
        assertEquals(EMPTY_RESULT_DATA_ACCESS.getErrorMessage(), response.getErrorMessage());
        assertEquals(String.format(NO_COURT_VENUES_FOUND_FOR_CLUSTER_ID, "0"),
                     response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldReturn404_WhenNoCourtTypeIdFound() {
        final var response = (ErrorResponse)
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.NOT_FOUND, "?court_type_id=000000",
                                                         LrdCourtVenueResponse[].class, path);
        assertEquals(EMPTY_RESULT_DATA_ACCESS.getErrorMessage(), response.getErrorMessage());
        assertEquals(String.format(NO_COURT_VENUES_FOUND_FOR_COURT_TYPE_ID, "0"),
                     response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldReturn404_WhenCourtVenueNameNotFound() {
        final var response = (ErrorResponse)
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.NOT_FOUND, "?court_venue_name=aaaabbbbcccc",
                                                         LrdCourtVenueResponse[].class, path);
        assertNotNull(response);
        assertEquals(EMPTY_RESULT_DATA_ACCESS.getErrorMessage(), response.getErrorMessage());
        assertEquals(String.format(NO_COURT_VENUES_FOUND_FOR_COURT_VENUE_NAME, "aaaabbbbcccc"),
                     response.getErrorDescription());
    }

    @Test
    void shouldReturn404_WhenCourtVenueWithParamsNotFound() throws
        JsonProcessingException {
        final var response = (ErrorResponse)
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.NOT_FOUND,
                "?epimms_id=ALL"
                    + "&is_hearing_location=N&is_case_management_location=N&location_type=CCBC"
                    + "&is_temporary_Location=N", LrdCourtVenueResponse[].class, path);

        assertNotNull(response);
        assertEquals(EMPTY_RESULT_DATA_ACCESS.getErrorMessage(), response.getErrorMessage());
        assertEquals("There are no court venues found", response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldReturn404_WhenCourtVenueNotFound_WithEpimmsID_AndCourtTypeID() {
        final var response = (ErrorResponse)
            lrdApiClient.retrieveResponseForGivenRequest(HttpStatus.NOT_FOUND, "?epimms_id=815833&court_type_id=0000",
                                                         LrdCourtVenueResponse[].class, path);
        assertNotNull(response);
        assertEquals(EMPTY_RESULT_DATA_ACCESS.getErrorMessage(), response.getErrorMessage());
        assertEquals(String.format(NO_COURT_VENUES_FOUND_FOR_FOR_EPIMMS_ID, "[815833]"),
                     response.getErrorDescription());
    }

    @Test
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = mapKey, withFeature = false)
    void shouldNotRetrieveCourtVenues_WhenToggleOff_WithStatusCode_403() {
        String exceptionMessage = getToggledOffMessage();
        validateErrorResponse(
            (ErrorResponse) lrdApiClient
                .retrieveResponseForGivenRequest(HttpStatus.FORBIDDEN, "?epimms_id=815833",
                                                 LrdBuildingLocationResponse.class, path),
            exceptionMessage,
            exceptionMessage
        );
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void retrieveCourtVenues_UnauthorizedDueToNoBearerToken_ShouldReturnStatusCode401() {
        Response response =
            lrdApiClient.retrieveResponseForGivenRequest_NoBearerToken("1", path);

        assertNotNull(response);
        assertThat(response.getHeader("UnAuthorized-Token-Error")).contains("Authentication Exception");
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void retrieveBuildingLocations_UnauthorizedDueToNoS2SToken_ShouldReturnStatusCode401() {
        Response response =
            lrdApiClient.retrieveResponseForGivenRequest_NoS2SToken("1", path);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
    }
}
