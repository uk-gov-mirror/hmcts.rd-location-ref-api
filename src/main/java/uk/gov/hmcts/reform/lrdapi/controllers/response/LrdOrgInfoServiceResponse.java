package uk.gov.hmcts.reform.lrdapi.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.lrdapi.domain.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LrdOrgInfoServiceResponse {

    @JsonProperty
    protected Long serviceId;
    @JsonProperty
    protected String orgUnit;
    @JsonProperty
    protected String businessArea;
    @JsonProperty
    protected String subBusinessArea;
    @JsonProperty
    protected String jurisdiction;
    @JsonProperty
    protected String serviceDescription;
    @JsonProperty
    protected String serviceCode;
    @JsonProperty
    protected String serviceShortDescription;
    @JsonProperty
    protected String ccdServiceName;
    @JsonProperty
    protected LocalDateTime lastUpdate;

    @JsonProperty
    protected List<String> ccdCaseTypes;

    public LrdOrgInfoServiceResponse(Service service) {

        getLrdOrgInfoServiceResponse(service);
    }

    public void getLrdOrgInfoServiceResponse(Service service) {

        if (null != service) {
            this.serviceId = service.getServiceId();
            this.orgUnit = service.getOrgUnit().getDescription();
            this.businessArea = service.getOrgBusinessArea().getDescription();
            this.subBusinessArea = service.getOrgSubBusinessArea().getDescription();
            this.serviceCode = service.getServiceCode();
            this.serviceDescription = service.getServiceDescription();
            this.serviceShortDescription = service.getServiceShortDescription();
            this.jurisdiction = service.getJurisdiction().getDescription();
            if (!service.getServiceToCcdCaseTypeAssocs().isEmpty()) {
                this.ccdServiceName = service.getServiceToCcdCaseTypeAssocs().get(0).getCcdServiceName();
            }
            this.ccdCaseTypes = service.getServiceToCcdCaseTypeAssocs().stream().map(serviceToCcdCaseTypeAssoc ->

                          serviceToCcdCaseTypeAssoc.getCcdCaseType())
                .collect(toList());
            this.lastUpdate = service.getLastUpdate();
        }

    }
}
