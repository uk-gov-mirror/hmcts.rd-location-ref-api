package uk.gov.hmcts.reform.lrdapi.controllers.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.lrdapi.domain.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LrdOrgInfoServiceResponse {

    @JsonProperty("service_id")
    protected Long serviceId;

    @JsonProperty("org_unit")
    protected String orgUnit;

    @JsonProperty("business_area")
    protected String businessArea;

    @JsonProperty("sub_business_area")
    protected String subBusinessArea;
    @JsonProperty
    protected String jurisdiction;

    @JsonProperty("service_description")
    protected String serviceDescription;

    @JsonProperty("service_code")
    protected String serviceCode;

    @JsonProperty("service_short_description")
    protected String serviceShortDescription;

    @JsonProperty("ccd_service_name")
    protected String ccdServiceName;

    @JsonProperty("last_update")
    protected String lastUpdate;

    @JsonProperty("ccd_case_types")
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
            this.lastUpdate = service.getLastUpdate().toString();
        }

    }
}
