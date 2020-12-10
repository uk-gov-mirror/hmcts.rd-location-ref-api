package uk.gov.hmcts.reform.lrdapi.controllers.response;

import org.junit.Test;
import uk.gov.hmcts.reform.lrdapi.domain.Jurisdiction;
import uk.gov.hmcts.reform.lrdapi.domain.OrgBusinessArea;
import uk.gov.hmcts.reform.lrdapi.domain.OrgSubBusinessArea;
import uk.gov.hmcts.reform.lrdapi.domain.OrgUnit;
import uk.gov.hmcts.reform.lrdapi.domain.Service;
import uk.gov.hmcts.reform.lrdapi.domain.ServiceToCcdCaseTypeAssoc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LrdOrgInfoServiceResponseTest {

    @Test
    public void testLrdOrgInfoServiceResponse() {

        OrgUnit orgUnit = new OrgUnit(1L, "orgUnit");
        Service service = new Service();
        service.setServiceId(1L);
        service.setOrgUnit(orgUnit);
        service.setServiceCode("AAA1");
        service.setServiceDescription("Insolvency");
        service.setServiceShortDescription("Insolvency");
        OrgSubBusinessArea orgSubBusArea = new OrgSubBusinessArea(1L, "OrgSubBusinessArea");
        OrgBusinessArea orgBusinessArea = new OrgBusinessArea(1L, "BusinessArea");
        Jurisdiction jurisdiction = new Jurisdiction(1L, "Jurisdiction");
        service.setOrgSubBusinessArea(orgSubBusArea);
        service.setOrgBusinessArea(orgBusinessArea);
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
        LrdOrgInfoServiceResponse response = new LrdOrgInfoServiceResponse(service);
        assertThat(response).isNotNull();
    }
}
