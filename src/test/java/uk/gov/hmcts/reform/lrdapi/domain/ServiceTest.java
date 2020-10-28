package uk.gov.hmcts.reform.lrdapi.domain;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceTest {

    @Test
    public void testService() {

        OrgUnit orgUnit = new OrgUnit(1L,"orgUnit");
        Service service = new Service();
        service.setServiceId(1L);
        service.setOrgUnitId(1L);
        service.setOrgUnit(orgUnit);
        service.setBusinessAreaId(1L);
        service.setServiceCode("AAA1");
        service.setJurisdictionId(1L);
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
        List<ServiceToCcdCaseTypeAssoc> serviceToCcdCaseTypeAssocs = new ArrayList<>();
        serviceToCcdCaseTypeAssocs.add(serviceToCcdCaseTypeAssoc);
        service.setServiceToCcdCaseTypeAssocs(serviceToCcdCaseTypeAssocs);

        assertThat(service).isNotNull();
        assertThat(service.getOrgUnitId()).isEqualTo(1L);
        assertThat(service.getBusinessAreaId()).isEqualTo(1L);
        assertThat(service.getServiceCode()).isEqualTo("AAA1");
        assertThat(service.getJurisdiction()).isNotNull();
        assertThat(service.getJurisdiction().getJurisdictionId()).isEqualTo(1L);
        assertThat(service.getServiceDescription()).isEqualTo("Insolvency");
        assertThat(service.getServiceShortDescription()).isEqualTo("Insolvency");
        assertThat(service.getLastUpdate()).isNotNull();
        assertThat(service.getServiceToCcdCaseTypeAssocs()).isNotNull();
        assertThat(service.getServiceToCcdCaseTypeAssocs().size()).isEqualTo(1);
    }
}
