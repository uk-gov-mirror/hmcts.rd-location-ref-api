package uk.gov.hmcts.reform.lrdapi.provider;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.VersionSelector;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.lrdapi.controllers.LrdApiController;
import uk.gov.hmcts.reform.lrdapi.domain.Jurisdiction;
import uk.gov.hmcts.reform.lrdapi.domain.OrgBusinessArea;
import uk.gov.hmcts.reform.lrdapi.domain.OrgSubBusinessArea;
import uk.gov.hmcts.reform.lrdapi.domain.OrgUnit;
import uk.gov.hmcts.reform.lrdapi.domain.Service;
import uk.gov.hmcts.reform.lrdapi.domain.ServiceToCcdCaseTypeAssoc;
import uk.gov.hmcts.reform.lrdapi.repository.ServiceRepository;
import uk.gov.hmcts.reform.lrdapi.repository.ServiceToCcdCaseTypeAssocRepositry;
import uk.gov.hmcts.reform.lrdapi.service.impl.LrdServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Provider("referenceData_location")
@PactBroker(scheme = "${PACT_BROKER_SCHEME:http}",
    host = "${PACT_BROKER_URL:localhost}",
    port = "${PACT_BROKER_PORT:80}", consumerVersionSelectors = {
    @VersionSelector(tag = "master")})
@ContextConfiguration(classes = {LrdApiController.class, LrdServiceImpl.class})
@TestPropertySource(properties = {"loggingComponentName=LrdApiProviderTest"})
@IgnoreNoPactsToVerify
public class LrdApiProviderTest {

    @MockBean
    ServiceRepository serviceRepository;

    @MockBean
    ServiceToCcdCaseTypeAssocRepositry serviceToCcdCaseTypeAssocRepositry;

    @Autowired
    LrdApiController lrdApiController;


    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        if (context != null) {
            context.verifyInteraction();
        }
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        //System.getProperties().setProperty("pact.verifier.publishResults", "true");
        MockMvcTestTarget testTarget = new MockMvcTestTarget();
        testTarget.setControllers(
            lrdApiController);
        if (context != null) {
            context.setTarget(testTarget);
        }

    }

    @State({"Organisational Service details exist"})
    public void toReturnOrganisationalServiceDetails() {
        OrgUnit orgUnit = new OrgUnit(1L, "orgUnit");
        Service service = new Service();
        service.setServiceId(1L);
        service.setOrgUnit(orgUnit);
        service.setServiceCode("AAA1");
        service.setServiceDescription("Insolvency");
        service.setServiceShortDescription("Insolvency");
        OrgSubBusinessArea orgSubBusArea =
            new OrgSubBusinessArea(1L, "OrgSubBusinessArea");
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
        service.setServiceToCcdCaseTypeAssocs(Collections.singletonList(serviceToCcdCaseTypeAssoc));
        List<Service> services = new ArrayList<>();
        services.add(service);
        when(serviceRepository.findByServiceCode(any())).thenReturn(service);
        when(serviceRepository.findAll()).thenReturn(services);
        when(serviceToCcdCaseTypeAssocRepositry.findByCcdCaseTypeIgnoreCase(any()))
            .thenReturn(serviceToCcdCaseTypeAssoc);

    }

}
