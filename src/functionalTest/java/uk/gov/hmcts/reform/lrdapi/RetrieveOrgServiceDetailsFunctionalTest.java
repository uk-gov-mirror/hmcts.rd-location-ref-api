package uk.gov.hmcts.reform.lrdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.lrdapi.controllers.advice.ErrorResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.lrdapi.util.CustomSerenityRunner;
import uk.gov.hmcts.reform.lrdapi.util.ToggleEnable;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.lrdapi.util.CustomSerenityRunner.getFeatureFlagName;
import static uk.gov.hmcts.reform.lrdapi.util.FeatureConditionEvaluation.FORBIDDEN_EXCEPTION_LD;

@RunWith(CustomSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
@ActiveProfiles("functional")
public class RetrieveOrgServiceDetailsFunctionalTest extends AuthorizationFunctionalTest {

    public static final String mapKey = "LrdApiController.retrieveOrgServiceDetails";

    @SuppressWarnings("unchecked")
    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    public void returnsOrgServiceDetailsByServiceCodeWithStatusCode_200() throws JsonProcessingException {
        List<LrdOrgInfoServiceResponse> responses = (List<LrdOrgInfoServiceResponse>)
            lrdApiClient.retrieveOrgServiceInfoByServiceCodeOrCaseTypeOrAll(HttpStatus.OK, "?serviceCode=AAA6");
        assertThat(responses.size()).isPositive();
    }

    @SuppressWarnings("unchecked")
    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    public void returnsOrgServiceDetailsByCcdCaseTypeWithStatusCode_200() throws JsonProcessingException {

        List<LrdOrgInfoServiceResponse> responses = (List<LrdOrgInfoServiceResponse>)
            lrdApiClient.retrieveOrgServiceInfoByServiceCodeOrCaseTypeOrAll(
                HttpStatus.OK,
                "?ccdCaseType=MoneyClaimCase"
            );

        assertThat(responses.size()).isEqualTo(1);
        responseVerification(responses);
    }

    @SuppressWarnings("unchecked")
    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    public void returnsOrgServiceDetailsByDefaultAll_200() throws JsonProcessingException {

        List<LrdOrgInfoServiceResponse> responses = (List<LrdOrgInfoServiceResponse>)
            lrdApiClient.retrieveOrgServiceInfoByServiceCodeOrCaseTypeOrAll(HttpStatus.OK, "");

        assertThat(responses.size()).isPositive();
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = false)
    public void should_retrieve_403_when_Api_toggled_off() {
        String exceptionMessage = getFeatureFlagName().concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD);
        validateErrorResponse(
            (ErrorResponse) lrdApiClient.retrieveOrgServiceInfoByServiceCodeOrCaseTypeOrAll(
                HttpStatus.FORBIDDEN, ""),
            exceptionMessage,
            exceptionMessage
        );
    }

    private void responseVerification(List<LrdOrgInfoServiceResponse> responses) throws JsonProcessingException {

        responses.forEach(response -> {
            assertThat(response.getServiceId()).isPositive();
            assertThat(response.getBusinessArea()).isEqualToIgnoringCase("Civil, Family and Tribunals");
            assertThat(response.getOrgUnit()).isEqualToIgnoringCase("HMCTS");
            assertThat(response.getCcdServiceName()).isNotNull();
            assertThat(response.getServiceCode()).isEqualTo("AAA6");
            assertThat(response.getJurisdiction()).isEqualToIgnoringCase("Civil");
            assertThat(response.getLastUpdate()).isNotNull();
            assertThat(response.getServiceDescription()).isEqualToIgnoringCase("Specified Money Claims");
            assertThat(response.getServiceShortDescription()).isEqualToIgnoringCase("Specified Money Claims");
            assertThat(response.getSubBusinessArea()).isEqualToIgnoringCase("Civil and Family");
            assertThat(response.getCcdCaseTypes().size()).isPositive();

        });
    }

    public void validateErrorResponse(ErrorResponse errorResponse, String expectedErrorMessage,
                                      String expectedErrorDescription) {
        assertThat(errorResponse.getErrorDescription()).isEqualTo(expectedErrorDescription);
        assertThat(errorResponse.getErrorMessage()).isEqualTo(expectedErrorMessage);
    }
}
