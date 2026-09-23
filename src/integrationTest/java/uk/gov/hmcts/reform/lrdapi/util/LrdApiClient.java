package uk.gov.hmcts.reform.lrdapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.lrdapi.controllers.advice.ErrorResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdBuildingLocationBySearchResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdBuildingLocationResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdCourtVenueResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdCourtVenuesByServiceCodeResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdRegionResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static uk.gov.hmcts.reform.lrdapi.SpringBootIntegrationTest.getObjectMapper;
import static uk.gov.hmcts.reform.lrdapi.util.JwtTokenUtil.generateAuthToken;
import static uk.gov.hmcts.reform.lrdapi.util.JwtTokenUtil.generateS2SToken;

@Slf4j
@PropertySource(value = "/integrationTest/resources/application-test.yml")
public class LrdApiClient {

    private static final String APP_BASE_PATH = "/refdata/location";

    private static  String JWT_TOKEN = null;
    private final Integer lrdApiPort;
    private final ObjectMapper objectMapper = getObjectMapper();
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
        ResponseEntity<Object> responseEntity = getRequest(
            APP_BASE_PATH + "/orgServices?serviceCode={serviceCode}", expectedClass, serviceCode);
        return mapApiResponse(responseEntity,expectedClass);
    }

    public Object findOrgServiceDetailsByCcdCaseType(String ccdCaseType, Class expectedClass) throws
        JsonProcessingException {

        ResponseEntity<Object> responseEntity = getRequest(
            APP_BASE_PATH + "/orgServices?ccdCaseType={ccdCaseType}", expectedClass, ccdCaseType);
        return mapApiResponse(responseEntity,expectedClass);
    }

    public Object findOrgServiceDetailsByCcdServiceName(String ccdServiceNames, Class expectedClass) throws
        JsonProcessingException {

        ResponseEntity<Object> responseEntity = getRequest(
            APP_BASE_PATH + "/orgServices?ccdServiceNames={ccdServiceNames}", expectedClass, ccdServiceNames);
        return mapApiResponse(responseEntity,expectedClass);
    }

    public Object findOrgServiceDetailsByDefaultAll(Class expectedClass) throws
        JsonProcessingException {
        ResponseEntity<Object> responseEntity = getRequest(
            APP_BASE_PATH + "/orgServices", expectedClass, "");
        return mapApiResponse(responseEntity,expectedClass);
    }

    public Object retrieveResponseForGivenRequest(String queryParam, Class<?> clazz, String path)
        throws JsonProcessingException {
        ResponseEntity<Object> responseEntity =
            getRequest(APP_BASE_PATH + path + queryParam, clazz, "");
        return mapBuildingLocationResponse(responseEntity, clazz);
    }

    public Object retrieveCourtVenueResponseForGivenRequest(String queryParam, Class<?> clazz, String path)
        throws JsonProcessingException {
        ResponseEntity<Object> responseEntity = null;
        if (StringUtils.isNotBlank(queryParam)) {
            responseEntity = getRequest(APP_BASE_PATH + path + queryParam, clazz, "");
        } else {
            responseEntity = getRequest(APP_BASE_PATH + path, clazz, "");
        }
        return mapCourtVenueResponse(responseEntity, clazz);
    }

    public Object findRegionDetailsByDescription(String region, Class expectedClass) throws
        JsonProcessingException {
        ResponseEntity<Object> responseEntity = getRequest(
            APP_BASE_PATH + "/regions?region={region}", expectedClass, region);
        return mapRegionResponse(responseEntity,expectedClass);
    }

    public Object findRegionDetailsById(String regionId, Class expectedClass) throws
        JsonProcessingException {
        ResponseEntity<Object> responseEntity = getRequest(
            APP_BASE_PATH + "/regions?regionId={regionId}", expectedClass, regionId);
        return mapRegionResponse(responseEntity,expectedClass);
    }

    public Object findCourtVenuesByServiceCode(String serviceCode, Class expectedClass) throws
        JsonProcessingException {
        ResponseEntity<Object> responseEntity = getRequest(
            APP_BASE_PATH + "/court-venues/services?service_code={service_code}", expectedClass, serviceCode);
        return mapCourtVenuesByServiceCodeResponse(responseEntity,expectedClass);
    }

    public Object findCourtVenuesBySearchString(String queryParam, Class<?> clazz, String path) throws
        JsonProcessingException {
        ResponseEntity<Object> responseEntity =
            getRequest(APP_BASE_PATH + path + queryParam, clazz, "");
        return mapCourtVenuesBySearchStringResponse(responseEntity, clazz);
    }

    public Object findBuildingLocationBySearchString(String queryParam, Class<?> clazz, String path) throws
        JsonProcessingException {
        ResponseEntity<Object> responseEntity =
            getRequest(APP_BASE_PATH + path + queryParam, clazz, "");
        return mapBuildingLocationBySearchResponse(responseEntity, clazz);
    }

    private Object mapApiResponse(ResponseEntity<Object> responseEntity, Class expectedClass) throws
        JsonProcessingException {

        HttpStatusCode status = responseEntity.getStatusCode();
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

    private Object mapRegionResponse(ResponseEntity<Object> responseEntity, Class expectedClass) throws
        JsonProcessingException {

        HttpStatusCode status = responseEntity.getStatusCode();
        if (status.is2xxSuccessful()) {
            return Arrays.asList((LrdRegionResponse[]) objectMapper.convertValue(
                responseEntity.getBody(), expectedClass));
        } else {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("response_body",  objectMapper.readValue(
                responseEntity.getBody().toString(), ErrorResponse.class));
            errorResponseMap.put("http_status", status);
            return errorResponseMap;
        }
    }

    private Object mapBuildingLocationResponse(ResponseEntity<Object> responseEntity, Class clazz)
        throws JsonProcessingException {

        HttpStatusCode status = responseEntity.getStatusCode();

        if (status.is2xxSuccessful()) {
            if (clazz.isArray()) {
                return Arrays.asList((LrdBuildingLocationResponse[])
                                         objectMapper.convertValue(responseEntity.getBody(), clazz));
            } else {
                return objectMapper.convertValue(responseEntity.getBody(), clazz);
            }
        } else {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("response_body",  objectMapper.readValue(
                responseEntity.getBody().toString(), ErrorResponse.class));
            errorResponseMap.put("http_status", status);
            return errorResponseMap;
        }
    }

    private Object mapBuildingLocationBySearchResponse(ResponseEntity<Object> responseEntity, Class clazz)
        throws JsonProcessingException {

        HttpStatusCode status = responseEntity.getStatusCode();

        if (status.is2xxSuccessful()) {
            if (clazz.isArray()) {
                return Arrays.asList((LrdBuildingLocationBySearchResponse[])
                                         objectMapper.convertValue(responseEntity.getBody(), clazz));
            } else {
                return objectMapper.convertValue(responseEntity.getBody(), clazz);
            }
        } else {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("response_body",  objectMapper.readValue(
                responseEntity.getBody().toString(), ErrorResponse.class));
            errorResponseMap.put("http_status", status);
            return errorResponseMap;
        }
    }

    private Object mapCourtVenuesByServiceCodeResponse(ResponseEntity<Object> responseEntity, Class clazz) throws
        JsonProcessingException {

        HttpStatusCode status = responseEntity.getStatusCode();

        if (status.is2xxSuccessful()) {
            if (clazz.isArray()) {
                return Arrays.asList((LrdCourtVenuesByServiceCodeResponse[])
                                         objectMapper.convertValue(responseEntity.getBody(), clazz));
            } else {
                return objectMapper.convertValue(responseEntity.getBody(), clazz);
            }
        } else {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("response_body",  objectMapper.readValue(
                responseEntity.getBody().toString(), ErrorResponse.class));
            errorResponseMap.put("http_status", status);
            return errorResponseMap;
        }

    }

    private Object mapCourtVenuesBySearchStringResponse(ResponseEntity<Object> responseEntity, Class clazz) throws
        JsonProcessingException {

        HttpStatusCode status = responseEntity.getStatusCode();

        if (status.is2xxSuccessful()) {
            return objectMapper.convertValue(responseEntity.getBody(), clazz);
        } else {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("response_body", objectMapper.readValue(
                responseEntity.getBody().toString(), ErrorResponse.class));
            errorResponseMap.put("http_status", status);
            return errorResponseMap;
        }

    }

    private Object mapCourtVenueResponse(ResponseEntity<Object> responseEntity, Class clazz)
        throws JsonProcessingException {

        HttpStatusCode status = responseEntity.getStatusCode();

        if (status.is2xxSuccessful()) {
            if (clazz.isArray()) {
                return Arrays.asList((LrdCourtVenueResponse[])
                                         objectMapper.convertValue(responseEntity.getBody(), clazz));
            } else {
                return objectMapper.convertValue(responseEntity.getBody(), clazz);
            }
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
            return ResponseEntity.status(ex.getStatusCode().value()).body(ex.getResponseBodyAsString());
        }
        return responseEntity;
    }

    private HttpHeaders getMultipleAuthHeaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        if (StringUtils.isBlank(JWT_TOKEN)) {

            JWT_TOKEN = generateS2SToken(serviceName);
        }
        headers.add("ServiceAuthorization", JWT_TOKEN);
        String bearerToken = "Bearer ".concat(getBearerToken(UUID.randomUUID().toString()));
        headers.add("Authorization", bearerToken);

        return headers;
    }

    private String getBearerToken(String userId) {

        return generateAuthToken(issuer, false, userId, null);

    }

}
