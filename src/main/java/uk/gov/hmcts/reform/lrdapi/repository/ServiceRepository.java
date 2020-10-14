package uk.gov.hmcts.reform.lrdapi.repository;

import feign.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.lrdapi.domain.Service;
import uk.gov.hmcts.reform.lrdapi.domain.ServiceToCcdCaseTypeAssoc;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    Service findByServiceCode(String serviceCode);

   /* @Query(value = "select * from service s ,service_to_ccd_case_type_assoc sc  where s. service_code= :serviceCode",
        nativeQuery = true)
    Service findByServiceCode(@Param("serviceCode") String serviceCode);*/

    @EntityGraph(value = "Service.alljoins")
    List<Service> findAll();

}
