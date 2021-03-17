package uk.gov.hmcts.reform.lrdapi.repository;

import org.junit.Test;
import uk.gov.hmcts.reform.lrdapi.domain.ServiceToCcdCaseTypeAssoc;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceToCcdCaseTypeAssocRepositoryTest {

    ServiceToCcdCaseTypeAssocRepositry serviceToCcdCaseTypeAssocRepositry
        = mock(ServiceToCcdCaseTypeAssocRepositry.class);

    @Test
    public void findByServiceCodeTest() {
        when(serviceToCcdCaseTypeAssocRepositry.findByCcdCaseTypeIgnoreCase(anyString()))
            .thenReturn(new ServiceToCcdCaseTypeAssoc());
        assertNotNull(serviceToCcdCaseTypeAssocRepositry.findByCcdCaseTypeIgnoreCase(anyString()));
    }
}
