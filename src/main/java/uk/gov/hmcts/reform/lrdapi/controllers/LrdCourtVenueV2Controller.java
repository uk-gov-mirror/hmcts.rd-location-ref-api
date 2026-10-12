package uk.gov.hmcts.reform.lrdapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdCourtVenueV2Response;
import uk.gov.hmcts.reform.lrdapi.domain.CourtVenueRequestParam;
import uk.gov.hmcts.reform.lrdapi.service.CourtVenueService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.lrdapi.util.ValidationUtils.trimCourtVenueRequestParam;
import static uk.gov.hmcts.reform.lrdapi.util.ValidationUtils.validateCourtTypeId;
import static uk.gov.hmcts.reform.lrdapi.util.ValidationUtils.validateCourtVenueFilters;
import static uk.gov.hmcts.reform.lrdapi.util.ValidationUtils.validateSearchString;
import static uk.gov.hmcts.reform.lrdapi.util.ValidationUtils.validateServiceCodes;

@RequestMapping(
    path = "/refdata/location/v2/court-venues"
)
@RestController
@Slf4j
public class LrdCourtVenueV2Controller {

    private static final String DEPRECATED_COURT_TYPE_ID = "Deprecated parameter, please use `service_code` instead.";
    private static final String WARNING_COURT_VENUE_ID = "&#9888; **Note: the `court_venue_id` property returned "
        + "in this response is deprecated and will be removed in a future release. "
        + "Please use `mrd_venue_id` instead.**";

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    @Autowired
    CourtVenueService courtVenueService;

    @Operation(
        summary = "This endpoint will be used for V2 Court Venues search based on partial query.",
        description = "No roles required to access this API<br/><br/>" + WARNING_COURT_VENUE_ID,
        security = {
            @SecurityRequirement(name = "ServiceAuthorization"),
            @SecurityRequirement(name = "Authorization")
        }
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved list of V2 Court Venues for the request provided",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = LrdCourtVenueV2Response.class)))
    )
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Forbidden Error: Access denied", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    @GetMapping(
        path = "/venue-search",
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<LrdCourtVenueV2Response>> retrieveCourtVenuesBySearchStringV2(
        @RequestParam(value = "search-string")
        @Parameter(name = "search-string",
            description = "Alphabets, Numeric And Special characters(_@.,’&-() ) "
                + "only allowed and String should contain minimum three chars.",
            required = true)
        String searchString,
        @RequestParam(value = "court-type-id", required = false)
        @Parameter(name = "court-type-id",
            description = "Alphabets and Numeric values only allowed in comma separated format. <br/><br/>"
                + DEPRECATED_COURT_TYPE_ID, deprecated = true)
        String courtTypeId,
        @RequestParam(value = "service_code", required = false)
        @Parameter(name = "service_code",
            description = "Alphabets and Numeric values only allowed in comma separated format")
        String serviceCode,
        @RequestParam(value = "is_hearing_location", required = false)
        @Parameter(name = "is_hearing_location", description = "Allowed values are \"Y\" or \"N\"")
        String isHearingLocation,
        @RequestParam(value = "is_case_management_location", required = false)
        @Parameter(name = "is_case_management_location", description = "Allowed values are \"Y\" or \"N\"")
        String isCaseManagementLocation,
        @RequestParam(value = "location_type", required = false)
        @Parameter(name = "location_type", description = "Allowed values include CTSC, NBC, Court, CCBC")
        String locationType,
        @RequestParam(value = "is_temporary_location", required = false)
        @Parameter(name = "is_temporary_location", description = "Allowed values are \"Y\" or \"N\"")
        String isTemporaryLocation,
        @RequestParam(value = "is_nightingale_court", required = false)
        @Parameter(name = "is_nightingale_court", description = "Allowed values are \"Y\" or \"N\"")
        String isNightingaleCourt,
        @RequestParam(value = "is_district_registry", required = false)
        @Parameter(name = "is_district_registry", description = "Allowed values are \"Y\" or \"N\"")
        String isDistrictRegistry,
        @RequestParam(value = "is_appeal_centre", required = false)
        @Parameter(name = "is_appeal_centre", description = "Allowed values are \"Y\" or \"N\"")
        String isAppealCentre
    ) {
        log.info("{} : Inside retrieveCourtVenuesBySearchStringV2", loggingComponentName);
        String trimmedSearchString = searchString.strip();
        validateSearchString(trimmedSearchString);
        if (StringUtils.isNotBlank(courtTypeId)) {
            validateCourtTypeId(courtTypeId);
        }

        if (StringUtils.isNotBlank(serviceCode)) {
            validateServiceCodes(serviceCode);
        }

        CourtVenueRequestParam requestParam = CourtVenueRequestParam
            .builder()
            .isHearingLocation(isHearingLocation)
            .isCaseManagementLocation(isCaseManagementLocation)
            .locationType(locationType)
            .isTemporaryLocation(isTemporaryLocation)
            .isNightingaleCourt(isNightingaleCourt)
            .isDistrictRegistry(isDistrictRegistry)
            .isAppealCentre(isAppealCentre)
            .build();
        CourtVenueRequestParam result = trimCourtVenueRequestParam(requestParam);

        validateCourtVenueFilters(result);

        log.info("{} : Calling retrieveCourtVenuesBySearchStringV2", loggingComponentName);
        var lrdCourtVenueResponses = courtVenueService.retrieveCourtVenuesBySearchStringV2(
            trimmedSearchString, courtTypeId, serviceCode, result);
        return ResponseEntity.status(HttpStatus.OK).body(lrdCourtVenueResponses);
    }

}
