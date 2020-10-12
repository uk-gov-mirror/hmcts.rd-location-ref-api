package uk.gov.hmcts.reform.lrdapi.client;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import uk.gov.hmcts.reform.lrdapi.idam.IdamOpenIdClient;


@Slf4j
public class LrdApiClient {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String SERVICE_HEADER = "ServiceAuthorization";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final String lrdApiUrl;
    private final String s2sToken;
    private IdamOpenIdClient idamOpenIdClient;


    public LrdApiClient(
        String lrdApiUrl,
        String s2sToken,
        IdamOpenIdClient idamOpenIdClient) {
        this. lrdApiUrl = lrdApiUrl;
        this.idamOpenIdClient = idamOpenIdClient;
        this.s2sToken = s2sToken;
    }


    public String getWelcomePage() {
        return withUnauthenticatedRequest()
            .get("/")
            .then()
            .statusCode(OK.value())
            .and()
            .extract()
            .response()
            .body()
            .asString();
    }

    public String getHealthPage() {
        return withUnauthenticatedRequest()
            .get("/health")
            .then()
            .statusCode(OK.value())
            .and()
            .extract()
            .response()
            .body()
            .asString();
    }



    @SuppressWarnings("unchecked")
    public Map<String, Object> retrieveServiceCodeInfo(String serviceCode) {
        Response response = getMultipleAuthHeadersInternal()
            .body("")
            .get("/refdata/location/orgServices?serviceCode=" + serviceCode)
            .andReturn();

        log.info("Retrieve  response: " + response.asString());

        response.then()
            .assertThat()
            .statusCode(OK.value());

        return response.body().as(Map.class);
    }



    private RequestSpecification withUnauthenticatedRequest() {
        return SerenityRest.given()
            .relaxedHTTPSValidation()
            .baseUri(lrdApiUrl)
            .header("Content-Type", APPLICATION_JSON_VALUE)
            .header("Accepts", APPLICATION_JSON_VALUE);
    }

    private RequestSpecification getS2sTokenHeaders() {
        return withUnauthenticatedRequest()
            .header(SERVICE_HEADER, "Bearer " + s2sToken);
    }

    private RequestSpecification getMultipleAuthHeadersInternal() {
        return  getMultipleAuthHeaders(idamOpenIdClient.getOpenIdToken());
    }

    public RequestSpecification getMultipleAuthHeaders(String userToken) {
        return SerenityRest.with()
            .relaxedHTTPSValidation()
            .baseUri(lrdApiUrl)
            .header("Content-Type", APPLICATION_JSON_VALUE)
            .header("Accepts", APPLICATION_JSON_VALUE)
            .header(SERVICE_HEADER, "Bearer " + s2sToken)
            .header(AUTHORIZATION_HEADER, "Bearer " + userToken);
    }

    @SuppressWarnings("unused")
    private JsonNode parseJson(String jsonString) throws IOException {
        return mapper.readTree(jsonString);
    }
}
