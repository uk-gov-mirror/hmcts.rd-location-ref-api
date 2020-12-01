package uk.gov.hmcts.reform.lrdapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.lrdapi.controllers.advice.ErrorResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdOrgInfoServiceResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static uk.gov.hmcts.reform.lrdapi.util.JwtTokenUtil.generateToken;

@Slf4j
@PropertySource(value = "/integrationTest/resources/application-test.yml")
public class LrdApiClient {

    private static final String APP_BASE_PATH = "/refdata/location/orgServices";

    private static  String JWT_TOKEN = null;
    private final Integer lrdApiPort;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private String baseUrl;

    private String issuer;
    private long expiration;

    @Value("${s2s-authorised.services}")
    private String serviceName;

    public LrdApiClient(int port, String issuer, Long tokenExpirationInterval) {
        this.lrdApiPort = port;
        this.baseUrl = "http://localhost:" + lrdApiPort + APP_BASE_PATH;
        this.issuer = issuer;
        this.expiration = tokenExpirationInterval;
    }

    public Object findOrgServiceDetailsByServiceCode(String serviceCode, Class expectedClass) throws
        JsonProcessingException {
        ResponseEntity<Object> responseEntity = getRequest(APP_BASE_PATH + "?serviceCode={serviceCode}",
                                                           expectedClass, serviceCode);
        return mapApiResponse(responseEntity,expectedClass);
    }

    public Object findOrgServiceDetailsByCcdCaseType(String ccdCaseType, Class expectedClass) throws
        JsonProcessingException {

        ResponseEntity<Object> responseEntity = getRequest(APP_BASE_PATH + "?ccdCaseType={ccdCaseType}",
                                                           expectedClass, ccdCaseType);
        return mapApiResponse(responseEntity,expectedClass);
    }

    public Object findOrgServiceDetailsByDefaultAll(Class expectedClass) throws
        JsonProcessingException {
        ResponseEntity<Object> responseEntity = getRequest(APP_BASE_PATH,
                                                           expectedClass, "");
        return mapApiResponse(responseEntity,expectedClass);
    }

    private Object mapApiResponse(ResponseEntity<Object> responseEntity, Class expectedClass) throws
        JsonProcessingException {

        HttpStatus status = responseEntity.getStatusCode();
        if (status.is2xxSuccessful()) {
            return Arrays.asList((LrdOrgInfoServiceResponse[]) objectMapper.convertValue(
                responseEntity.getBody(), expectedClass));
        } else {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("response_body",  objectMapper.readValue(
                responseEntity.getBody().toString(), ErrorResponse.class));
            errorResponseMap.put("http_status", status);
            return errorResponseMap;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ResponseEntity<Object> getRequest(String uriPath,Class clasz,Object... params) {

        ResponseEntity<Object> responseEntity;
        try {
            HttpEntity<?> request = new HttpEntity<>(getMultipleAuthHeaders());
            responseEntity = restTemplate
                .exchange("http://localhost:" + lrdApiPort + uriPath,
                          HttpMethod.GET,
                          request,
                          clasz,
                          params);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getRawStatusCode()).body(ex.getResponseBodyAsString());
        }

        return responseEntity;
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
        if (StringUtils.isBlank(JWT_TOKEN)) {

            JWT_TOKEN = generateDummyS2SToken(serviceName);
        }
        headers.add("ServiceAuthorization", JWT_TOKEN);
        String bearerToken = "Bearer ".concat(getBearerToken(UUID.randomUUID().toString()));
        headers.add("Authorization", bearerToken);

        return headers;
    }

    private final String getBearerToken(String userId) {

        return generateToken(issuer, expiration, userId);

    }

    public static String generateDummyS2SToken(String serviceName) {
        return Jwts.builder()
            .setSubject(serviceName)
            .setIssuedAt(new Date())
            .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.encode("AA"))
            .compact();
    }

}
