package uk.gov.hmcts.reform.lrdapi.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static uk.gov.hmcts.reform.lrdapi.util.JwtTokenUtil.generateToken;

@Slf4j
@PropertySource(value = "/integrationTest/resources/application.yml")
public class LrdApiClient {

    private static final String APP_BASE_PATH = "/refdata/location/orgServices";

    private static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI"
        + "6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    private final Integer lrdApiPort;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private String baseUrl;

    private String issuer;
    private long expiration;

    public LrdApiClient(int port, String issuer, Long tokenExpirationInterval) {
        this.lrdApiPort = port;
        this.baseUrl = "http://localhost:" + lrdApiPort + APP_BASE_PATH;
        this.issuer = issuer;
        this.expiration = tokenExpirationInterval;
    }

    public Map<String, Object> findOrgServiceDetailsByServiceCode(String serviceCode) {
        return getRequest(APP_BASE_PATH + "?serviceCode={serviceCode}", serviceCode);
    }

    public Map<String, Object> findOrgServiceDetailsByCcdCaseType(String ccdCaseType) {
        return getRequest(APP_BASE_PATH + "?ccdCaseType={ccdCaseType}", ccdCaseType);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Map<String, Object> getRequest(String uriPath, Object... params) {

        ResponseEntity<Map> responseEntity;

        try {

            HttpEntity<?> request = new HttpEntity<>(getMultipleAuthHeaders());
            responseEntity = restTemplate
                .exchange("http://localhost:" + lrdApiPort + uriPath,
                          HttpMethod.GET,
                          request,
                          Map.class,
                          params);
        } catch (HttpStatusCodeException ex) {
            HashMap<String, Object> statusAndBody = new HashMap<>(2);
            statusAndBody.put("http_status", String.valueOf(ex.getRawStatusCode()));
            statusAndBody.put("response_body", ex.getResponseBodyAsString());
            return statusAndBody;
        }

        return getResponse(responseEntity);
    }

    private Map getResponse(ResponseEntity<Map> responseEntity) {

        Map response = objectMapper
            .convertValue(
                responseEntity.getBody(),
                Map.class);

        response.put("http_status", responseEntity.getStatusCode().toString());
        response.put("headers", responseEntity.getHeaders().toString());

        return response;
    }

    private HttpHeaders getMultipleAuthHeaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        headers.add("ServiceAuthorization", JWT_TOKEN);
        String bearerToken = "Bearer ".concat(getBearerToken(UUID.randomUUID().toString()));
        headers.add("Authorization", bearerToken);

        return headers;
    }

    private final String getBearerToken(String userId) {

        return generateToken(issuer, expiration, userId);

    }

}
