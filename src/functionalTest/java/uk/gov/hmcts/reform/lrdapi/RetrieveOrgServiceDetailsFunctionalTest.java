package uk.gov.hmcts.reform.lrdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdOrgInfoServiceResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
@ActiveProfiles("functional")
public class RetrieveOrgServiceDetailsFunctionalTest  extends AuthorizationFunctionalTest {



    @Test
    public void returnsOrgServiceDetailsByServiceCodeWithStatusCode_200() throws JsonProcessingException {

        List<LrdOrgInfoServiceResponse> responses = (List<LrdOrgInfoServiceResponse>)
            lrdApiClient.retrieveOrgServiceInfoByServiceCodeOrCaseTypeOrAll(HttpStatus.OK,"?serviceCode=AAA1");

        assertThat(responses.size()).isEqualTo(1);
        responseVerification(responses);
    }

    @Test
    public void returnsOrgServiceDetailsByCcdCaseTypeWithStatusCode_200() throws JsonProcessingException {

        List<LrdOrgInfoServiceResponse> responses = (List<LrdOrgInfoServiceResponse>)
            lrdApiClient.retrieveOrgServiceInfoByServiceCodeOrCaseTypeOrAll(HttpStatus.OK,"?ccdCaseType=CCDCASETYPE1");

        assertThat(responses.size()).isEqualTo(1);
        responseVerification(responses);
    }

    @Test
    public void returnsOrgServiceDetailsByDefaultAll_200() throws JsonProcessingException {

        List<LrdOrgInfoServiceResponse> responses = (List<LrdOrgInfoServiceResponse>)
            lrdApiClient.retrieveOrgServiceInfoByServiceCodeOrCaseTypeOrAll(HttpStatus.OK,"");

        assertThat(responses.size()).isGreaterThanOrEqualTo(1);
    }

    private void responseVerification(List<LrdOrgInfoServiceResponse> responses) throws JsonProcessingException {

        responses.stream().forEach(response -> {
            assertThat(response.getServiceId()).isEqualTo(1L);
            assertThat(response.getBusinessArea()).isEqualToIgnoringCase("Civil, Family and Tribunals");
            assertThat(response.getOrgUnit()).isEqualToIgnoringCase("HMCTS");
            assertThat(response.getCcdServiceName()).isNotNull();
            assertThat(response.getServiceCode()).isEqualTo("AAA1");
            assertThat(response.getJurisdiction()).isEqualToIgnoringCase("Civil");
            assertThat(response.getLastUpdate()).isNotNull();
            assertThat(response.getServiceDescription()).isEqualToIgnoringCase("Civil Enforcement");
            assertThat(response.getServiceShortDescription()).isEqualToIgnoringCase("Civil Enforcement");
            assertThat(response.getSubBusinessArea()).isEqualToIgnoringCase("Civil and Family");
            assertThat(response.getCcdCaseTypes().size()).isEqualTo(0);

        });

    }
}
