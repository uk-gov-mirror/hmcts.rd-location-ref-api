package uk.gov.hmcts.reform.lrdapi.service;

import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdOrgInfoServiceResponse;

import java.util.List;

public interface LrdService {

    List<LrdOrgInfoServiceResponse> findByServiceCodeOrCcdCaseTypeOrDefault(String serviceCode, String ccdCode);
}
