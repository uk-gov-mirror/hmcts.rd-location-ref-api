package uk.gov.hmcts.reform.lrdapi.util;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringIntegrationSerenityRunner.class)
public class RetrieveOrgServiceDetailsIntegrationTest extends LrdAuthorizationEnabledIntegrationTest {

    @SuppressWarnings("unchecked")
    @Test
    public void returnsOrgServiceDetailsByServiceCodeWithStatusCode200() {

        Map<String, Object> orgResponse = lrdApiClient.findOrgServiceDetailsByServiceCode("AAA1");
        assertThat(orgResponse.get("http_status")).isEqualTo("200 OK");

    }

    @Test
    public void returnsOrgServiceDetailsByCcdCaseTypeCode200() {

        Map<String, Object> orgResponse = lrdApiClient.findOrgServiceDetailsByCcdCaseType("ccdCaseType1");
        assertThat(orgResponse.get("http_status")).isEqualTo("200 OK");

    }
}
