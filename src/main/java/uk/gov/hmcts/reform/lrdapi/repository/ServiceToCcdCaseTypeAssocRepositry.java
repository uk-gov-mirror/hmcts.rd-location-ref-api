package uk.gov.hmcts.reform.lrdapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.lrdapi.domain.ServiceToCcdCaseTypeAssoc;

public interface ServiceToCcdCaseTypeAssocRepositry extends JpaRepository<ServiceToCcdCaseTypeAssoc, Long> {

    ServiceToCcdCaseTypeAssoc findByCcdCaseType(String ccdCaseType);
}
