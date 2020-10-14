package uk.gov.hmcts.reform.lrdapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
//import java.util.ArrayList;
//import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
//import javax.persistence.OneToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Size;

@Entity(name = "service")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SequenceGenerator(name = "service_seq", sequenceName = "service_seq", allocationSize = 1)
/*@NamedEntityGraph(
        name = "Service.alljoins",
        attributeNodes = {
                @NamedAttributeNode(value = "serviceToCcdCaseTypeAssoc"),
        }
)*/
public class Service implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_seq")
    @Column(name = "service_id")
    private Long  serviceId;

    @NonNull
    @Column(name = "org_unit_id")
    private Long orgUnitId;

    @NonNull
    @Column(name = "business_area_id")
    private Long businessAreaId;

    @NonNull
    @Column(name = "sub_business_area_id")
    private Long subBusinessAreaId;

    @NonNull
    @Column(name = "jurisdiction_id")
    private Long  jurisdictionId;

    @Column(name = "service_code")
    @Size(max = 16)
    private String serviceCode;

    @Column(name = "service_description")
    @Size(max = 512)
    private String serviceDescription;

    @Column(name = "service_short_description")
    @Size(max = 256)
    private String  serviceShortDescription;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    //@Fetch(FetchMode.SUBSELECT)
    /*@OneToMany(targetEntity = ServiceToCcdCaseTypeAssoc.class)
    @JoinColumn(name = "service_code", insertable = false, updatable = false)
    private List<ServiceToCcdCaseTypeAssoc> serviceToCcdCaseTypeAssocs = new ArrayList<>();*/

    @OneToOne
    @JoinColumn(name = "jurisdiction_id", insertable = false, updatable = false)
    private Jurisdiction jurisdiction;

    @OneToOne
    @JoinColumn(name = "business_area_id", nullable = false, insertable = false, updatable = false)
    private OrgBusinessArea orgBusinessArea;

    @OneToOne
    @JoinColumn(name = "org_unit_id", nullable = false, insertable = false, updatable = false)
    private OrgUnit orgUnit;

    @OneToOne
    @JoinColumn(name = "sub_business_area_id", nullable = false, insertable = false, updatable = false)
    private OrgSubBusinessArea orgSubBusinessArea;

    /* public Service(Long orgUnitId,
            Long businessAreaId,
            Long subBusinessAreaId,
            Long jurisdictionId,
            String serviceCode,
            String serviceDescription,
            String serviceShortDescription,
            LocalDateTime lastUpdate) {

        //this.serviceId = serviceId;
        this.orgUnitId = orgUnitId;
        this.businessAreaId = businessAreaId;
        this.jurisdictionId = jurisdictionId;
        this.subBusinessAreaId = subBusinessAreaId;
        this.serviceCode = serviceCode;
        this.serviceDescription = serviceDescription;
        this.serviceShortDescription = serviceShortDescription;
        this.lastUpdate = lastUpdate;
    }*/

   /* public void addServiceToCcdCaseTypeAssoc(ServiceToCcdCaseTypeAssoc serviceToCcdCaseTypeAssoc) {
        serviceToCcdCaseTypeAssocs.add(serviceToCcdCaseTypeAssoc);
    }*/

}
