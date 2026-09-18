package uk.gov.hmcts.reform.lrdapi.security;

import io.restassured.specification.RequestSpecification;
import uk.gov.hmcts.reform.lrdapi.controller.BaseAuthorisedTestIntegration;

import static uk.gov.hmcts.reform.lrdapi.util.TestAuthenticationUtils.getJwtHeaders;

public class BaseSecurityIntegrationTest extends BaseAuthorisedTestIntegration {

    protected static final String VALID_ISSUER_1 = "http://localhost:5062/o";
    protected static final String VALID_ISSUER_2 = "https://secondary-idam.platform.hmcts.net";
    protected static final String ROGUE_ISSUER = "https://rogue-issuer.com";

    protected RequestSpecification jwtRequest(
            String issuer,
            boolean expired)
            throws Exception {

        return getRequestSpecification(getJwtHeaders(issuer, expired));
    }

    protected RequestSpecification unexpiredJwt(
            String issuer)
            throws Exception {

        return jwtRequest(issuer, false);
    }

    protected RequestSpecification expiredJwt(
            String issuer)
            throws Exception {

        return jwtRequest(issuer, true);
    }
}
