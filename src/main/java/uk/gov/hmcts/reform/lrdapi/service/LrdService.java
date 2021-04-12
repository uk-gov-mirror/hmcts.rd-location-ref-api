package uk.gov.hmcts.reform.lrdapi.service;

import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdOrgInfoServiceResponse;

import java.util.List;

public interface LrdService {

    List<LrdOrgInfoServiceResponse> retrieveOrgServiceDetails(String serviceCode, String ccdCode, String ccdServiceName);
}
