package uk.gov.hmcts.reform.lrdapi.util;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.reform.lrdapi.controllers.advice.ResourceNotFoundException;

@Slf4j
public class LrdApiUtil {

    private LrdApiUtil() {

    }

    public static String getJurisdictionDescription(int jurisdictionId) {

        switch (jurisdictionId) {
            case 1:
                return "Civil";
            case 2:
                return "Family";
            case 3:
                return "General Regulatory Chamber";
            case 4:
                return "Social Entitlement Chamber";
            case 5:
                return "Health, Education and Social Care Chamber";
            case 6:
                return "Tax Chamber";
            case 7:
                return "War Pensions and Armed Forces Compensation Chamber";
            case 8:
                return "Immigration and Asylum Chamber";
            case 9:
                return "Property Chamber";
            case 10:
                return "Employment Tribunerals";
            default:
                throw new ResourceNotFoundException("Unknown Jurisdiction value " + jurisdictionId);
        }
    }

    public static String getOrgUnitDescription(int orgUnitId) {

        switch (orgUnitId) {
            case 1:
                return "HMCTS";
            default:
                throw new ResourceNotFoundException("Unknown OrgUnit value " + orgUnitId);
        }
    }

    public static String getOrgBusinessAreaIdDescription(int businessAreaId) {

        switch (businessAreaId) {
            case 1:
                return "Civil, Family and Tribunals";
            default:
                throw new ResourceNotFoundException("Unknown OrgBusinessAreaId value " + businessAreaId);
        }
    }

    public static String getOrgSubBusinessAreaIdDescription(int orgSubBusinessAreaId) {

        switch (orgSubBusinessAreaId) {
            case 1:
                return "Civil and Family";
            case 2:
                return "Tribunals";
            default:
                throw new ResourceNotFoundException("Unknown orgSubBusinessAreaId value " + orgSubBusinessAreaId);
        }
    }
}
