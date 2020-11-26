package uk.gov.hmcts.reform.lrdapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

@Entity(name = "org_business_area")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(name = "org_business_area_seq", sequenceName = "org_business_area_seq", allocationSize = 1)
@Getter
@Setter
public class OrgBusinessArea implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_business_area_seq")
    @Column(name = "business_area_id")
    private Long businessAreaId;

    @Column(name = "description")
    private String  description;

    @OneToMany(targetEntity = Service.class, mappedBy = "orgBusinessArea")
    private List<Service> services = new ArrayList<>();

    public OrgBusinessArea(Long businessAreaId, String  description) {
        this.businessAreaId = businessAreaId;
        this.description = description;
    }
}
