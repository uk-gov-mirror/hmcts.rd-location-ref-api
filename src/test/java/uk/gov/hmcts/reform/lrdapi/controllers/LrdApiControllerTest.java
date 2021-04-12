package uk.gov.hmcts.reform.lrdapi.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.lrdapi.controllers.advice.InvalidRequestException;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.lrdapi.oidc.JwtGrantedAuthoritiesConverter;
import uk.gov.hmcts.reform.lrdapi.service.LrdService;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LrdApiControllerTest {

    @InjectMocks
    private LrdApiController lrdApiController;

    LrdService lrdServiceMock;

    List<LrdOrgInfoServiceResponse> lrdOrgInfoServiceResponse = new ArrayList<>();
    private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverterMock;
    private UserInfo userInfoMock;
    HttpServletRequest httpRequest = mock(HttpServletRequest.class);
    String serviceCode;
    String ccdCaseType;
    String ccdServiceName;

    @Before
    public void setUp() throws Exception {

        lrdServiceMock = mock(LrdService.class);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRetrieveOrgServiceDetailsByServiceCode() {
        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        serviceCode = "AAA1";
        when(lrdServiceMock.retrieveOrgServiceDetails(any(), any(), any())).thenReturn(lrdOrgInfoServiceResponse);
        ResponseEntity<?> actual = lrdApiController
            .retrieveOrgServiceDetails(serviceCode, ccdCaseType, ccdServiceName);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
        verify(lrdServiceMock, times(1)).retrieveOrgServiceDetails(any(), any(), any());
    }

    @Test
    public void testRetrieveOrgServiceDetailsByCcdCaseType() {
        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        ccdCaseType = "ccdCaseType1";
        when(lrdServiceMock.retrieveOrgServiceDetails(any(), any(), any())).thenReturn(lrdOrgInfoServiceResponse);
        ResponseEntity<?> actual = lrdApiController
            .retrieveOrgServiceDetails(serviceCode, ccdCaseType, ccdServiceName);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
        verify(lrdServiceMock, times(1)).retrieveOrgServiceDetails(any(), any(), any());
    }

    @Test
    public void testRetrieveOrgServiceDetailsByDefaultRequestParamsNull() {
        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        when(lrdServiceMock.retrieveOrgServiceDetails(any(), any(), any())).thenReturn(lrdOrgInfoServiceResponse);
        ResponseEntity<?> actual = lrdApiController
            .retrieveOrgServiceDetails(serviceCode, ccdCaseType, ccdServiceName);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
        verify(lrdServiceMock, times(1)).retrieveOrgServiceDetails(any(), any(), any());
    }

    @Test(expected = InvalidRequestException.class)
    public void testRetrieveOrgServiceDetailsShouldThrowExceptionWhenBothParamValuesPresent() {
        final HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;
        serviceCode = "AAA1";
        ccdCaseType = "ccdCaseType1";
        ResponseEntity<?> actual = lrdApiController
            .retrieveOrgServiceDetails(serviceCode, ccdCaseType, ccdServiceName);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
    }

    //1. Validate that only 1 out of 3 query params are being passed. Use null and empty values for testing
    @Test(expected = InvalidRequestException.class)
    public void testRetrieveOrgServiceDetailsShouldThrowExceptionWhenMultipleParamValuesPresent() {
        final HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;
        serviceCode = "AAA1";
        ccdCaseType = "ccdCaseType1";
        ccdServiceName = "fpla";
        ResponseEntity<?> actual = lrdApiController
            .retrieveOrgServiceDetails(serviceCode, ccdCaseType, ccdServiceName);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
    }

    @Test
    public void testRetrieveOrgServiceDetailsShouldPassForNullAndBlankValuesScenario1() {
        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        serviceCode = "";
        ccdCaseType = null;
        ccdServiceName = "fpla";
        ResponseEntity<?> actual = lrdApiController
            .retrieveOrgServiceDetails(serviceCode, ccdCaseType, ccdServiceName);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
    }

    @Test
    public void testRetrieveOrgServiceDetailsShouldPassForNullAndBlankValuesScenario2() {
        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        serviceCode = "";
        ccdCaseType = null;
        ccdServiceName = "";
        ResponseEntity<?> actual = lrdApiController
            .retrieveOrgServiceDetails(serviceCode, ccdCaseType, ccdServiceName);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
    }
}
