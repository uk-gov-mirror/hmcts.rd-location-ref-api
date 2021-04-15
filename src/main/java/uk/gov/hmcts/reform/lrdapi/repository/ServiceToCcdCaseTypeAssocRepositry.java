package uk.gov.hmcts.reform.lrdapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.lrdapi.domain.ServiceToCcdCaseTypeAssoc;

import java.util.List;

public interface ServiceToCcdCaseTypeAssocRepositry extends JpaRepository<ServiceToCcdCaseTypeAssoc, Long> {

    ServiceToCcdCaseTypeAssoc findByCcdCaseTypeIgnoreCase(String ccdCaseType);

    List<ServiceToCcdCaseTypeAssoc> findByCcdServiceNameInIgnoreCase(List<String> ccdServiceName);
}
