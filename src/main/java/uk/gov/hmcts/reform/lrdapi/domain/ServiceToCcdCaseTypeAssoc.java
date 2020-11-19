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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Size;


@Entity(name = "service_to_ccd_case_type_assoc")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(name = "service_to_ccd_case_type_assoc_seq", sequenceName = "service_to_ccd_case_type_assoc_seq",
    allocationSize = 1)
@Getter
@Setter
public class ServiceToCcdCaseTypeAssoc implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_to_ccd_case_type_assoc_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "service_code")
    @Size(max = 16)
    private String serviceCode;

    @Column(name = "ccd_service_name")
    @Size(max = 256)
    private String ccdServiceName;

    @Column(name = "ccd_case_type")
    @Size(max = 256)
    private String  ccdCaseType;

    @ManyToOne
    @JoinColumn(name = "service_code",referencedColumnName = "service_code", insertable = false, updatable = false)
    private Service service;

}
