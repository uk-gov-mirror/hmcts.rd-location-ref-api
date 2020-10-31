package uk.gov.hmcts.reform.lrdapi.repository;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.lrdapi.domain.ServiceToCcdCaseTypeAssoc;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Integration")})
@DataJpaTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
public class ServiceToCcdCaseTypeAssocRepositoryTest {

    @Autowired
    ServiceToCcdCaseTypeAssocRepositry serviceToCcdCaseTypeAssocRepositry;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testOrgServiceDetailsByCcdCaseType() {
        ServiceToCcdCaseTypeAssoc serviceToCcdCaseTypeAssoc = serviceToCcdCaseTypeAssocRepositry
            .findByCcdCaseType("CCDCASETYPE1");
        assertThat(serviceToCcdCaseTypeAssoc).isNotNull();
        assertThat(serviceToCcdCaseTypeAssoc.getServiceCode()).isNotNull();
    }
}
