package uk.gov.hmcts.reform.lrdapi.domain;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceToCcdCaseTypeAssocTest {

    @Test
    public void testServiceToCcdCaseTypeAssoc() {
        OrgUnit orgUnit = new OrgUnit(1L,"orgUnit");

        Service service = new Service();
        service.setServiceId(1L);
        service.setOrgUnit(orgUnit);
//        service.setBusinessAreaId(1L);
        service.setServiceCode("AAA1");
        // service.setJurisdictionId(1L);
        service.setServiceDescription("Insolvency");
        service.setServiceShortDescription("Insolvency");
        service.setLastUpdate(LocalDateTime.now());

        ServiceToCcdCaseTypeAssoc serviceToCcdCaseTypeAssoc = new ServiceToCcdCaseTypeAssoc();
        serviceToCcdCaseTypeAssoc.setId(1L);
        serviceToCcdCaseTypeAssoc.setCcdCaseType("CCDCASETYPE1");
        serviceToCcdCaseTypeAssoc.setCcdServiceName("CCDSERVICENAME");
        serviceToCcdCaseTypeAssoc.setService(service);
        List<Service> services = new ArrayList<>();
        services.add(service);
        assertThat(serviceToCcdCaseTypeAssoc.getId()).isEqualTo(1L);
        assertThat(serviceToCcdCaseTypeAssoc.getCcdCaseType()).isEqualTo("CCDCASETYPE1");
        assertThat(serviceToCcdCaseTypeAssoc.getCcdServiceName()).isEqualTo("CCDSERVICENAME");
        assertThat(serviceToCcdCaseTypeAssoc.getService()).isNotNull();
        assertThat(service.getLastUpdate()).isNotNull();
        assertThat(service.getServiceToCcdCaseTypeAssocs()).isNotNull();
        assertThat(services.size()).isEqualTo(1);
    }
}
