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

    @Before
    public void setUp() throws Exception {

        lrdServiceMock = mock(LrdService.class);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRetrieveOrgServiceDetailsByServiceCode() {
        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        String serviceCode = "AAA1";
        String ccdCaseType = null;
        when(lrdServiceMock.findByServiceCodeOrCcdCaseTypeOrDefault(any(),any())).thenReturn(lrdOrgInfoServiceResponse);
        ResponseEntity<?> actual = lrdApiController
            .retrieveOrgServiceDetailsByServiceCodeOrCcdCaseType(serviceCode,ccdCaseType);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
        verify(lrdServiceMock, times(1)).findByServiceCodeOrCcdCaseTypeOrDefault(any(),any());
    }

    @Test
    public void testRetrieveOrgServiceDetailsByCcdCaseType() {
        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        String serviceCode = null;
        String ccdCaseType = "ccdCaseType1";
        when(lrdServiceMock.findByServiceCodeOrCcdCaseTypeOrDefault(any(),any())).thenReturn(lrdOrgInfoServiceResponse);
        ResponseEntity<?> actual = lrdApiController
            .retrieveOrgServiceDetailsByServiceCodeOrCcdCaseType(serviceCode,ccdCaseType);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
        verify(lrdServiceMock, times(1)).findByServiceCodeOrCcdCaseTypeOrDefault(any(),any());
    }

    @Test
    public void testRetrieveOrgServiceDetailsByDefaultRequestParamsNull() {
        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        String serviceCode = null;
        String ccdCaseType = null;
        when(lrdServiceMock.findByServiceCodeOrCcdCaseTypeOrDefault(any(),any())).thenReturn(lrdOrgInfoServiceResponse);
        ResponseEntity<?> actual = lrdApiController
            .retrieveOrgServiceDetailsByServiceCodeOrCcdCaseType(serviceCode,ccdCaseType);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
        verify(lrdServiceMock, times(1)).findByServiceCodeOrCcdCaseTypeOrDefault(any(),any());
    }

    @Test(expected = InvalidRequestException.class)
    public void testRetrieveOrgServiceDetailsShouldThrowExceptionWhenBothParamValuesPresent() {
        final HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;
        String serviceCode = "AAA1";
        String ccdCaseType = "ccdCaseType1";
        when(lrdServiceMock.findByServiceCodeOrCcdCaseTypeOrDefault(any(),any())).thenReturn(lrdOrgInfoServiceResponse);
        ResponseEntity<?> actual = lrdApiController
            .retrieveOrgServiceDetailsByServiceCodeOrCcdCaseType(serviceCode,ccdCaseType);
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
    }

}
