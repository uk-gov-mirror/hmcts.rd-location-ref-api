package uk.gov.hmcts.reform.lrdapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.launchdarkly.sdk.server.LDClient;
import com.nimbusds.jose.JOSEException;
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

import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.lrdapi.util.KeyGenUtil.getRsaJwk;
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

    @RegisterExtension
    public static WireMockExtension mockHttpServerForOidc = new WireMockExtension(7000);

    @BeforeEach
    public void setup() throws Exception {
        stubAuthorisationDetails(SERVICE_NAME);
        stubIdamConfig();
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
                           .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                           .withBody(serviceName)
                   ));
    }


    public void stubIdamConfig() throws JsonProcessingException {

        mockHttpServerForOidc.stubFor(get(urlPathMatching("/o/.well-known/openid-configuration"))
                  .willReturn(aResponse()
                                  .withStatus(200)
                                  .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                                  .withBody(OBJECT_MAPPER.writeValueAsString(getOpenIdResponse()))
                  ));

        mockHttpServerForOidc.stubFor(get(urlPathMatching("/o/jwks"))
                  .willReturn(aResponse()
                                  .withStatus(200)
                                  .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                                  .withBody(getJwksResponse())
                  ));

    }

    private Map<String, Object> getOpenIdResponse() {
        LinkedHashMap<String,Object> data1 = new LinkedHashMap<>();
        data1.put("issuer", "http://localhost:" + mockHttpServerForOidc.port() + "/o");
        data1.put("jwks_uri", "http://localhost:" + mockHttpServerForOidc.port() + "/o/jwks");

        return data1;
    }

    private String getJwksResponse() {
        try {
            return "{"
                + "\"keys\": [" + getRsaJwk().toPublicJWK().toJSONString() + "]"
                + "}";

        } catch (JOSEException ex) {
            throw new RuntimeException(ex);
        }

    }
}
