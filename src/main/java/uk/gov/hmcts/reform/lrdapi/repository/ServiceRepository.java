package uk.gov.hmcts.reform.lrdapi.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.lrdapi.domain.Service;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    Service findByServiceCode(String serviceCode);

    @EntityGraph(value = "Service.alljoins")
    List<Service> findAll();

}
