package uk.gov.hmcts.reform.lrdapi.controllers.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.lrdapi.domain.Service;
import uk.gov.hmcts.reform.lrdapi.util.LrdApiUtil;

import java.time.LocalDateTime;
import java.util.List;

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

        this.serviceId = service.getServiceId();
        this.orgUnit = LrdApiUtil.getOrgUnitDescription(service.getOrgUnitId().intValue());
        this.businessArea = LrdApiUtil.getOrgBusinessAreaIdDescription(service.getBusinessAreaId().intValue());
        this.subBusinessArea = LrdApiUtil.getOrgBusinessAreaIdDescription(service.getSubBusinessAreaId().intValue());
        this.serviceCode = service.getServiceCode();
        this.serviceDescription = service.getServiceDescription();
        this.serviceShortDescription = service.getServiceShortDescription();
        this.jurisdiction = LrdApiUtil.getJurisdictionDescription(service.getJurisdictionId().intValue());
        this.lastUpdate = service.getLastUpdate();
    }
}
