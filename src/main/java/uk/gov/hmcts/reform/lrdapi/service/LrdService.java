package uk.gov.hmcts.reform.lrdapi.service;

import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdOrgInfoServiceResponse;

public interface LrdService {

    LrdOrgInfoServiceResponse findByServiceCode(String serviceCode, String ccdCode);
}
