package uk.gov.hmcts.reform.lrdapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name = "org_unit")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(name = "org_unit_seq", sequenceName = "org_unit_seq", allocationSize = 1)
@Getter
@Setter
public class OrgUnit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_unit_seq")
    @Column(name = "org_unit_id")
    private Long orgUnitId;

    @Column(name = "description")
    private String  description;

    public OrgUnit(Long orgUnitId, String  description) {
        this.orgUnitId = orgUnitId;
        this.description = description;
    }
}
