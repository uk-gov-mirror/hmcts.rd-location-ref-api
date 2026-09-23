package uk.gov.hmcts.reform.lrdapi;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.reform.lrdapi.repository.ServiceRepository;
import uk.gov.hmcts.reform.lrdapi.repository.ServiceToCcdCaseTypeAssocRepositry;
import uk.gov.hmcts.reform.lrdapi.service.impl.FeatureToggleServiceImpl;
import uk.gov.hmcts.reform.lrdapi.util.LrdApiClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.lrdapi.util.FeatureConditionEvaluation.SERVICE_AUTHORIZATION;
import static uk.gov.hmcts.reform.lrdapi.util.JwtTokenUtil.generateAuthToken;
import static uk.gov.hmcts.reform.lrdapi.util.JwtTokenUtil.generateS2SToken;

@Configuration
@ExtendWith(SerenityJUnit5Extension.class)
@TestPropertySource(properties = {"S2S_URL=http://127.0.0.1:8990", "IDAM_URL:http://127.0.0.1:5000"})
@DirtiesContext
public abstract class LrdAuthorizationEnabledIntegrationTest extends SpringBootIntegrationTest {

    public static final String LRD_SERVICE_NAME = "rd_location_ref_api";

    @Autowired
    protected ServiceRepository serviceRepository;

    @Autowired
    protected ServiceToCcdCaseTypeAssocRepositry serviceToCcdCaseTypeAssocRepositry;

    protected LrdApiClient lrdApiClient;

    @MockitoBean
    protected FeatureToggleServiceImpl featureToggleService;

    @Value("${oidc.issuer}")
    private String issuer;

    @Value("${oidc.expiration}")
    private long expiration;

    @Value("${idam.s2s-auth.microservice}")
    static String authorisedService;

    @BeforeEach
    public void setUpClient() {
        when(featureToggleService.isFlagEnabled(anyString(), anyString())).thenReturn(true);
        lrdApiClient = new LrdApiClient(port, issuer, expiration);
    }

    public static HttpHeaders getHttpHeaders(String issuer, boolean isExpired, String userId, String role) {
        HttpHeaders headers = new HttpHeaders();
        var userAuthToken = generateAuthToken(issuer, isExpired, userId, role);
        headers.setBearerAuth(userAuthToken);
        headers.add(SERVICE_AUTHORIZATION, "Bearer " + generateS2SToken(LRD_SERVICE_NAME));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}

