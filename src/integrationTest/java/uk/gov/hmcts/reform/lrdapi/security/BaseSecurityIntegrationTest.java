package uk.gov.hmcts.reform.lrdapi.security;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import uk.gov.hmcts.reform.lrdapi.LrdAuthorizationEnabledIntegrationTest;

public class BaseSecurityIntegrationTest extends LrdAuthorizationEnabledIntegrationTest {

    protected static final String VALID_ISSUER_1 = "http://localhost:5062/o";
    protected static final String VALID_ISSUER_2 = "https://secondary-idam.platform.hmcts.net";
    protected static final String ROGUE_ISSUER = "https://rogue-issuer.com";

    protected static final String REGIONS_URL = "/refdata/location/regions";

    protected RequestSpecification jwtRequest(
            String issuer,
            boolean expired) {
        return SerenityRest.given()
            .baseUri(testApplicationServer.getBaseUrl())
            .headers(getHttpHeaders(issuer, expired, null, "lrd-admin"));
    }

    protected RequestSpecification unexpiredJwt(String issuer) {
        return jwtRequest(issuer, false);
    }

    protected RequestSpecification expiredJwt(String issuer) {
        return jwtRequest(issuer, true);
    }
}
