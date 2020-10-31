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
import uk.gov.hmcts.reform.lrdapi.domain.Service;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Integration")})
@DataJpaTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
public class ServiceRepositoryTest {

    @Autowired
    ServiceRepository serviceRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindOrgServiceDetailsByServiceCode() {

        Service service = serviceRepository.findByServiceCode("AAA1");
        assertNotNull(service);
    }
}
