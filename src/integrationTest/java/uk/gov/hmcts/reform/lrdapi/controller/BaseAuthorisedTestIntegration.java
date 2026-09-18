package uk.gov.hmcts.reform.lrdapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.launchdarkly.sdk.server.LDClient;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.lrdapi.SpringBootIntegrationTest;
import uk.gov.hmcts.reform.lrdapi.config.LaunchDarklyConfiguration;
import uk.gov.hmcts.reform.lrdapi.util.WireMockExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static uk.gov.hmcts.reform.lrdapi.util.TestAuthenticationUtils.SERVICE_NAME;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("itest")
@ExtendWith(SpringExtension.class)
@WithTags({@WithTag("testType:Integration")})
@TestPropertySource(properties = {"S2S_URL=http://127.0.0.1:8990"})
public abstract class BaseAuthorisedTestIntegration extends SpringBootIntegrationTest {

    protected static final String BASEURL = "http://localhost";
    protected static final String REGIONS_URL = "/refdata/location/regions";

    public static final ObjectMapper OBJECT_MAPPER =
        new Jackson2ObjectMapperBuilder()
            .modules(new Jdk8Module(), new JavaTimeModule())
            .build();

    @LocalServerPort
    private int serverPort;

    @MockitoBean
    LaunchDarklyConfiguration launchDarklyConfiguration;

    @MockitoBean
    LDClient ldClient;

    @RegisterExtension
    public static final WireMockExtension s2sService = new WireMockExtension(8990);

    @BeforeEach
    public void setup() throws Exception {
        stubAuthorisationDetails(SERVICE_NAME);
    }

    protected RequestSpecification getRequestSpecification(HttpHeaders httpHeaders) {
        return SerenityRest.given()
                .baseUri(BASEURL)
                .port(serverPort)
                .headers(httpHeaders);
    }

    public void stubAuthorisationDetails(String serviceName) {
        s2sService.stubFor(get(urlPathEqualTo("/details"))
                   .willReturn(aResponse()
                           .withStatus(HttpStatus.OK.value())
                           .withHeader("Content-Type", "application/json")
                           .withBody(serviceName)
                   ));
    }
}
