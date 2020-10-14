package uk.gov.hmcts.reform.lrdapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.lrdapi.domain.Service;
import uk.gov.hmcts.reform.lrdapi.domain.ServiceToCcdCaseTypeAssoc;
import uk.gov.hmcts.reform.lrdapi.repository.ServiceRepository;
import uk.gov.hmcts.reform.lrdapi.repository.ServiceToCcdCaseTypeAssocRepositry;
import uk.gov.hmcts.reform.lrdapi.service.LrdService;

import java.util.List;


@org.springframework.stereotype.Service
@Slf4j
public class LrdServiceImpl implements LrdService {

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    ServiceToCcdCaseTypeAssocRepositry serviceToCcdCaseTypeAssocRepositry;

    @Override
    public LrdOrgInfoServiceResponse findByServiceCode(String serviceCode, String ccdCaseType) {

        Service servicePojo = null;
        ServiceToCcdCaseTypeAssoc serToCcdCaseType = null;
        List<Service> service = null;
        if (null != serviceCode && null == ccdCaseType) {

            servicePojo = serviceRepository.findByServiceCode(serviceCode);
            serToCcdCaseType = serviceToCcdCaseTypeAssocRepositry.findByServiceCode(serviceCode);

        } else if ((null == serviceCode && null != ccdCaseType)) {

            serToCcdCaseType = serviceToCcdCaseTypeAssocRepositry.findByCcdCaseType(ccdCaseType);


        } else {

            service = serviceRepository.findAll();
        }

        // Service servicePojo = serviceRepository.findByServiceCode(serviceCode);
        if (null == servicePojo) {

            throw new EmptyResultDataAccessException(1);
        }
        return new LrdOrgInfoServiceResponse(servicePojo);
    }
}
