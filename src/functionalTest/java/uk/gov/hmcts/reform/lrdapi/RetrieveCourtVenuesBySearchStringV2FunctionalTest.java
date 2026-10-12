package uk.gov.hmcts.reform.lrdapi;

import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.lib.util.serenity5.SerenityTest;
import uk.gov.hmcts.reform.lrdapi.controllers.advice.ErrorResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdCourtVenueV2Response;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.ErrorConstants.INVALID_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.INVALID_ADDITIONAL_FILTER;

@SerenityTest
@SpringBootTest
@WithTags({@WithTag("testType:Functional")})
@ActiveProfiles("functional")
class RetrieveCourtVenuesBySearchStringV2FunctionalTest extends AuthorizationFunctionalTest {

    private static final String PATH = "/v2/court-venues/venue-search";
    private static final String[] SEARCH_STRINGS = {"Abe", "Court", "Tribunal"};

    @Test
    void getCourtVenuesBySearchStringV2WithStatusCode_200() {
        SearchResult searchResult = findSearchResultWithServiceCode();

        LrdCourtVenueV2Response[] response = (LrdCourtVenueV2Response[])
            lrdApiClient.retrieveResponseForGivenRequest(
                HttpStatus.OK,
                "?search-string=" + searchResult.searchString() + "&service_code=" + searchResult.serviceCode(),
                LrdCourtVenueV2Response[].class,
                PATH
            );

        assertNotNull(response);
        assertThat(response).isNotEmpty();
        assertThat(response).extracting(LrdCourtVenueV2Response::getServiceCode).contains(searchResult.serviceCode());
        assertThat(response).extracting(LrdCourtVenueV2Response::getMrdVenueId).doesNotContainNull();
    }

    private SearchResult findSearchResultWithServiceCode() {
        for (String searchString : SEARCH_STRINGS) {
            LrdCourtVenueV2Response[] searchResponse = (LrdCourtVenueV2Response[])
                lrdApiClient.retrieveResponseForGivenRequest(
                    HttpStatus.OK,
                    "?search-string=" + searchString,
                    LrdCourtVenueV2Response[].class,
                    PATH
                );

            if (searchResponse != null) {
                for (LrdCourtVenueV2Response venue : searchResponse) {
                    if (isNotBlank(venue.getServiceCode())) {
                        return new SearchResult(searchString, venue.getServiceCode());
                    }
                }
            }
        }
        throw new AssertionError("No court venue test data with a service code found for search strings "
            + Arrays.toString(SEARCH_STRINGS));
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private record SearchResult(String searchString, String serviceCode) {
    }

    @ParameterizedTest
    @CsvSource({
        "is_case_management_location,is_case_management_location",
        "is_hearing_location,is_hearing_location",
        "is_temporary_location,is_temporary_location",
        "is_nightingale_court,is_nightingale_court",
        "is_district_registry,is_district_registry",
        "is_appeal_centre,is_appeal_centre"
    })
    void getCourtVenuesBySearchStringV2WithInvalidCourtUseFilterStatusCode_400(String filterName,
                                                                               String expectedDescription) {
        ErrorResponse response = (ErrorResponse)
            lrdApiClient.retrieveResponseForGivenRequest(
                HttpStatus.BAD_REQUEST,
                "?search-string=Abe&" + filterName + "=P",
                ErrorResponse.class,
                PATH
            );

        assertNotNull(response);
        assertEquals(INVALID_REQUEST_EXCEPTION.getErrorMessage(), response.getErrorMessage());
        assertEquals(String.format(INVALID_ADDITIONAL_FILTER, expectedDescription),
                     response.getErrorDescription());
    }
}
