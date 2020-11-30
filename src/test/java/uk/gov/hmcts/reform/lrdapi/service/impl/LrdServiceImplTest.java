package uk.gov.hmcts.reform.lrdapi.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.lrdapi.domain.Jurisdiction;
import uk.gov.hmcts.reform.lrdapi.domain.OrgBusinessArea;
import uk.gov.hmcts.reform.lrdapi.domain.OrgSubBusinessArea;
import uk.gov.hmcts.reform.lrdapi.domain.OrgUnit;
import uk.gov.hmcts.reform.lrdapi.domain.Service;
import uk.gov.hmcts.reform.lrdapi.domain.ServiceToCcdCaseTypeAssoc;
import uk.gov.hmcts.reform.lrdapi.repository.ServiceRepository;
import uk.gov.hmcts.reform.lrdapi.repository.ServiceToCcdCaseTypeAssocRepositry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LrdServiceImplTest {

    private final ServiceRepository serviceRepository = mock(ServiceRepository.class);

    private final ServiceToCcdCaseTypeAssocRepositry serToCcdCsTypeRep = mock(ServiceToCcdCaseTypeAssocRepositry.class);

    @InjectMocks
    LrdServiceImpl sut;

    List<LrdOrgInfoServiceResponse> lrdOrgInfoServiceResponses = new ArrayList<>();
    List<Service> services = new ArrayList<>();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        OrgUnit orgUnit = new OrgUnit(1L,"orgUnit");
        Service service = new Service();
        service.setServiceId(1L);
        service.setOrgUnit(orgUnit);
        service.setServiceCode("AAA1");
        service.setServiceDescription("Insolvency");
        service.setServiceShortDescription("Insolvency");
        OrgSubBusinessArea orgSubBusArea = new OrgSubBusinessArea(1L, "OrgSubBusinessArea");
        OrgBusinessArea orgBusinessArea = new OrgBusinessArea(1L, "BusinessArea");
        Jurisdiction jurisdiction = new Jurisdiction(1L, "Jurisdiction");
        service.setOrgBusinessArea(orgBusinessArea);
        service.setOrgSubBusinessArea(orgSubBusArea);
        service.setJurisdiction(jurisdiction);
        service.setLastUpdate(LocalDateTime.now());
        ServiceToCcdCaseTypeAssoc serviceToCcdCaseTypeAssoc = new ServiceToCcdCaseTypeAssoc();
        serviceToCcdCaseTypeAssoc.setId(1L);
        serviceToCcdCaseTypeAssoc.setCcdCaseType("CCDCASETYPE1");
        serviceToCcdCaseTypeAssoc.setCcdServiceName("CCDSERVICENAME");
        serviceToCcdCaseTypeAssoc.setService(service);
        services.add(service);
        when(serviceRepository.findByServiceCode(any())).thenReturn(service);
        when(serToCcdCsTypeRep.findByCcdCaseType(any())).thenReturn(serviceToCcdCaseTypeAssoc);
        when(serviceRepository.findAll()).thenReturn(services);

    }

    @Test
    public void testRetrieveAnOrgServiceDetailsByServiceCode() {
        String serviceCode = "AAA1";
        lrdOrgInfoServiceResponses = sut.findByServiceCodeOrCcdCaseTypeOrDefault(serviceCode,null);
        assertThat(lrdOrgInfoServiceResponses).isNotNull();
        assertThat(lrdOrgInfoServiceResponses.size()).isEqualTo(1);
        verify(serviceRepository, times(1)).findByServiceCode(serviceCode);

    }

    @Test
    public void testRetrieveAnOrgServiceDetailsByCcdCodeType() {
        String serviceCode = null;
        String ccdCaseType = "CCDCASETYPE1";
        lrdOrgInfoServiceResponses = sut.findByServiceCodeOrCcdCaseTypeOrDefault(serviceCode,ccdCaseType);
        assertThat(lrdOrgInfoServiceResponses).isNotNull();
        assertThat(lrdOrgInfoServiceResponses.size()).isEqualTo(1);
        verify(serToCcdCsTypeRep, times(1)).findByCcdCaseType(ccdCaseType);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testShouldThrowExceptionForUnKnownServiceCode() {
        String serviceCode = "serviceCode";
        String ccdCaseType = null;
        when(serviceRepository.findByServiceCode(any())).thenReturn(null);
        sut.findByServiceCodeOrCcdCaseTypeOrDefault(serviceCode,ccdCaseType);
        verify(serviceRepository,times(1)).findByServiceCode(any());
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testShouldThrowExceptionForUnKnownCcdCodeType() {
        String serviceCode = null;
        String ccdCaseType = "ccdcodeType";
        when(serToCcdCsTypeRep.findByCcdCaseType(any())).thenReturn(null);
        sut.findByServiceCodeOrCcdCaseTypeOrDefault(serviceCode,ccdCaseType);
        verify(serToCcdCsTypeRep,times(1)).findByCcdCaseType(any());
    }

    @Test
    public void testRetrieveAnOrgServiceDetailsByDefaultWithoutAnyQueryFormsInTheRequest() {
        String serviceCode = null;
        String ccdCaseType = null;
        lrdOrgInfoServiceResponses = sut.findByServiceCodeOrCcdCaseTypeOrDefault(null,null);
        assertThat(lrdOrgInfoServiceResponses).isNotNull();
        assertThat(lrdOrgInfoServiceResponses.size()).isEqualTo(1);
        verify(serviceRepository, times(1)).findAll();

    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testShouldThrowExceptionForEmptyServiceDetails() {
        String serviceCode = null;
        String ccdCaseType = null;
        when(serviceRepository.findAll()).thenReturn(null);
        lrdOrgInfoServiceResponses = sut.findByServiceCodeOrCcdCaseTypeOrDefault(null,null);
        assertThat(lrdOrgInfoServiceResponses).isNull();
        verify(serviceRepository,times(1)).findAll();
    }

}
