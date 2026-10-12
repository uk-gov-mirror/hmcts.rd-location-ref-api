package uk.gov.hmcts.reform.lrdapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.lrdapi.controllers.advice.InvalidRequestException;
import uk.gov.hmcts.reform.lrdapi.controllers.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdCourtVenueResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdCourtVenueV2Response;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdCourtVenuesByServiceCodeResponse;
import uk.gov.hmcts.reform.lrdapi.domain.CourtVenue;
import uk.gov.hmcts.reform.lrdapi.domain.CourtVenueRequestParam;
import uk.gov.hmcts.reform.lrdapi.repository.CourtVenueRepository;
import uk.gov.hmcts.reform.lrdapi.repository.projection.CourtVenueAddressProjection;
import uk.gov.hmcts.reform.lrdapi.repository.projection.CourtVenueContactProjection;
import uk.gov.hmcts.reform.lrdapi.repository.projection.CourtVenueNameProjection;
import uk.gov.hmcts.reform.lrdapi.repository.projection.CourtVenueReferenceCodeProjection;
import uk.gov.hmcts.reform.lrdapi.repository.projection.CourtVenueUrlProjection;
import uk.gov.hmcts.reform.lrdapi.repository.projection.CourtVenueUseProjection;
import uk.gov.hmcts.reform.lrdapi.service.CourtVenueService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.ALPHA_NUMERIC_REGEX;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.ALPHA_NUMERIC_REGEX_WITHOUT_UNDERSCORE;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.COMMA;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.COURT_STATUS_CLOSED;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.COURT_STATUS_OPEN;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.EXCEPTION_MSG_NO_VALID_EPIM_ID_PASSED;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.EXCEPTION_MSG_NO_VALID_MRD_VENUE_ID_PASSED;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.EXCEPTION_MSG_SERVICE_CODE_SPCL_CHAR;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.IS_CASE_MANAGEMENT_LOCATION_N;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.IS_CASE_MANAGEMENT_LOCATION_Y;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.IS_HEARING_LOCATION_N;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.IS_HEARING_LOCATION_Y;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.IS_TEMPORARY_LOCATION_N;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.IS_TEMPORARY_LOCATION_Y;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.MRD_VENUE_ID_REGEX;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.NO_COURT_VENUES_FOUND;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.NO_COURT_VENUES_FOUND_FOR_CLUSTER_ID;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.NO_COURT_VENUES_FOUND_FOR_COURT_TYPE_ID;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.NO_COURT_VENUES_FOUND_FOR_COURT_VENUE_NAME;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.NO_COURT_VENUES_FOUND_FOR_FOR_EPIMMS_ID;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.NO_COURT_VENUES_FOUND_FOR_MRD_VENUE_ID;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.NO_COURT_VENUES_FOUND_FOR_REGION_ID;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.NO_COURT_VENUES_FOUND_FOR_SERVICE_CODE;
import static uk.gov.hmcts.reform.lrdapi.util.ValidationUtils.checkForInvalidIdentifiersAndRemoveFromIdList;
import static uk.gov.hmcts.reform.lrdapi.util.ValidationUtils.checkIfValidCsvIdentifiersAndReturnList;
import static uk.gov.hmcts.reform.lrdapi.util.ValidationUtils.isListContainsTextIgnoreCase;
import static uk.gov.hmcts.reform.lrdapi.util.ValidationUtils.isRegexSatisfied;
import static uk.gov.hmcts.reform.lrdapi.util.ValidationUtils.trimCourtVenueRequestParam;
import static uk.gov.hmcts.reform.lrdapi.util.ValidationUtils.validateCourtVenueFilters;

@Slf4j
@Service
public class CourtVenueServiceImpl implements CourtVenueService {

    private static final String USE_APPEAL_CENTRE = "APPEAL_CENTRE";
    private static final String USE_CASE_MANAGEMENT = "CASE_MANAGEMENT";
    private static final String USE_DISTRICT_REGISTRY = "DISTRICT_REGISTRY";
    private static final String USE_HEARING = "HEARING";
    private static final String USE_NIGHTINGALE = "NIGHTINGALE";
    private static final String USE_TEMPORARY = "TEMPORARY";
    private static final String URL_TYPE_FACT = "FACT";
    private static final String URL_TYPE_SERVICE = "SERVICE";
    private static final String REFERENCE_CODE_VENUE_OU_CODE = "VENUE_OU_CODE";
    private static final String VALUE_Y = "Y";
    private static final String VALUE_N = "N";

    @Autowired
    CourtVenueRepository courtVenueRepository;

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    public static String validateServiceCode(String serviceCode) {
        String trimmedServiceCode = StringUtils.strip(serviceCode);

        if (isBlank(trimmedServiceCode)) {
            throw new InvalidRequestException("No service code provided");
        }
        if (isFalse(isRegexSatisfied(trimmedServiceCode, ALPHA_NUMERIC_REGEX_WITHOUT_UNDERSCORE))) {
            throw new InvalidRequestException(EXCEPTION_MSG_SERVICE_CODE_SPCL_CHAR);
        }
        return trimmedServiceCode;
    }

    @Override
    public LrdCourtVenuesByServiceCodeResponse retrieveCourtVenuesByServiceCode(String serviceCode) {

        String trimmedServiceCode = validateServiceCode(serviceCode);

        String serviceCodeIgnoreCase = trimmedServiceCode.toUpperCase();

        log.info("{} : Obtaining court venues for service code: {}", loggingComponentName, trimmedServiceCode);

        List<CourtVenue> courtVenues = courtVenueRepository.findByServiceCodeWithOpenCourtStatus(serviceCodeIgnoreCase);

        handleIfCourtVenuesEmpty(
            () -> isEmpty(courtVenues),
            "No court venues found for the given service code " + trimmedServiceCode,
            trimmedServiceCode
        );

        List<LrdCourtVenueResponse> courtVenueResponses = getCourtVenueListResponse(courtVenues);

        return new LrdCourtVenuesByServiceCodeResponse(courtVenues.get(0).getCourtType(),
                                                       courtVenueResponses, serviceCodeIgnoreCase);

    }

    @Override
    public List<LrdCourtVenueResponse> retrieveCourtVenuesBySearchString(String searchString, String courtTypeId,
                                                                         String serviceCodes,
                                                                         CourtVenueRequestParam requestParam) {
        log.info("{} : Obtaining court venue for search String: searchString: {}, courtTypeId: {}, "
                     + "serviceCodes: {}, isHearingLocation: {}, isCaseManagementLocation: {}, locationType: {}, "
                     + "isTemporaryLocation: {} ",
                 loggingComponentName, searchString, courtTypeId, serviceCodes, requestParam.getIsHearingLocation(),
                 requestParam.getIsCaseManagementLocation(),
                 requestParam.getLocationType(), requestParam.getIsTemporaryLocation());

        var result =  trimCourtVenueRequestParam(requestParam);
        validateCourtVenueFilters(result);

        List<String> courtTypeIdList = StringUtils.isEmpty(courtTypeId) ? null :
            Arrays.stream(courtTypeId.split(COMMA)).map(String::strip).toList();

        List<String> serviceCodeList = StringUtils.isEmpty(serviceCodes) ? null :
            Arrays.stream(serviceCodes.split(COMMA)).map(String::strip).toList();

        String isCaseManagementLocation = (StringUtils.isNotEmpty(result.getIsCaseManagementLocation()))
            ? result.getIsCaseManagementLocation().toUpperCase()
            : result.getIsCaseManagementLocation();

        String isHearingLocation = (StringUtils.isNotEmpty(result.getIsHearingLocation()))
            ? result.getIsHearingLocation().toUpperCase()
            : result.getIsHearingLocation();

        String locationType = (StringUtils.isNotEmpty(result.getLocationType()))
            ? result.getLocationType().toUpperCase()
            : result.getLocationType();

        String isTemporaryLocation = (StringUtils.isNotEmpty(result.getIsTemporaryLocation()))
            ? result.getIsTemporaryLocation().toUpperCase()
            : result.getIsTemporaryLocation();

        List<CourtVenue> courtVenues = courtVenueRepository.findBySearchStringAndCourtTypeId(
            searchString.toUpperCase(),
            courtTypeIdList,
            serviceCodeList,
            isCaseManagementLocation,
            isHearingLocation,
            locationType,
            isTemporaryLocation
        );

        return   getCourtVenueListResponse(courtVenues);
    }

    @Override
    public List<LrdCourtVenueV2Response> retrieveCourtVenuesBySearchStringV2(String searchString, String courtTypeId,
                                                                             String serviceCodes,
                                                                             CourtVenueRequestParam requestParam) {
        log.info("{} : Obtaining V2 court venue for search String: searchString: {}, courtTypeId: {}, "
                     + "serviceCodes: {}, isHearingLocation: {}, isCaseManagementLocation: {}, locationType: {}, "
                     + "isTemporaryLocation: {}, isNightingaleCourt: {}, isDistrictRegistry: {}, "
                     + "isAppealCentre: {} ",
                 loggingComponentName, searchString, courtTypeId, serviceCodes, requestParam.getIsHearingLocation(),
                 requestParam.getIsCaseManagementLocation(), requestParam.getLocationType(),
                 requestParam.getIsTemporaryLocation(), requestParam.getIsNightingaleCourt(),
                 requestParam.getIsDistrictRegistry(), requestParam.getIsAppealCentre());

        var result = trimCourtVenueRequestParam(requestParam);
        validateCourtVenueFilters(result);

        List<String> courtTypeIdList = StringUtils.isEmpty(courtTypeId) ? null :
            Arrays.stream(courtTypeId.split(COMMA)).map(String::strip).toList();

        List<String> serviceCodeList = StringUtils.isEmpty(serviceCodes) ? null :
            Arrays.stream(serviceCodes.split(COMMA)).map(String::strip).map(String::toUpperCase).toList();

        String locationType = (StringUtils.isNotEmpty(result.getLocationType()))
            ? result.getLocationType().toUpperCase()
            : result.getLocationType();

        List<CourtVenue> courtVenues = courtVenueRepository.findBySearchStringAndCourtTypeId(
            searchString.toUpperCase(),
            courtTypeIdList,
            serviceCodeList,
            null,
            null,
            locationType,
            null
        );

        if (courtVenues.isEmpty()) {
            return List.of();
        }

        return applyCourtUseFilters(buildCourtVenueV2Responses(getCourtVenueListResponse(courtVenues)), result);
    }

    @Override
    public List<LrdCourtVenueResponse> retrieveCourtVenueDetails(String epimmsIds, Integer courtTypeId,
                                                                 String serviceCode, Integer regionId,
                                                                 Integer clusterId, String courtVenueName,
                                                                 boolean epimmsIdWithCourtTypeOrServiceCodePresent,
                                                                 CourtVenueRequestParam courtVenueRequestParam) {
        return retrieveCourtVenueDetailsForV2(
            epimmsIds,
            null,
            courtTypeId,
            serviceCode,
            regionId,
            clusterId,
            courtVenueName,
            epimmsIdWithCourtTypeOrServiceCodePresent,
            courtVenueRequestParam
        );
    }

    private List<LrdCourtVenueResponse> retrieveCourtVenueDetailsForV2(String epimmsIds, String mrdVenueId,
                                                                       Integer courtTypeId, String serviceCode,
                                                                       Integer regionId, Integer clusterId,
                                                                       String courtVenueName,
                                                                       boolean epimmsIdWithCourtTypeOrServiceCode,
                                                                       CourtVenueRequestParam courtVenueRequestParam) {

        if (epimmsIdWithCourtTypeOrServiceCode) {
            return getLrdCourtVenueResponses(
                retrieveCourtVenuesByEpimmsIdAndCourtType(epimmsIds, courtTypeId, serviceCode,
                                                          courtVenueRequestParam),
                courtVenueRequestParam
            );

        }
        if (isNotBlank(epimmsIds)) {
            return getLrdCourtVenueResponses(
                retrieveCourtVenuesByEpimmsId(epimmsIds, courtVenueRequestParam),
                courtVenueRequestParam
            );

        }
        if (isNotBlank(mrdVenueId)) {
            return getLrdCourtVenueResponses(
                retrieveCourtVenuesByMrdVenueId(mrdVenueId, courtVenueRequestParam),
                courtVenueRequestParam
            );
        }
        if (isNotEmpty(serviceCode)) {
            log.info("{} : Obtaining court venues for service codes: {}", loggingComponentName, serviceCode);

            List<LrdCourtVenueResponse> lrdCourtVenueResponse =
                getAllCourtVenues(
                    () -> isCourtStatusFilterPresent(courtVenueRequestParam)
                        ? courtVenueRepository.findByServiceCode(serviceCode)
                        : courtVenueRepository.findByServiceCodeWithOpenCourtStatus(serviceCode),
                    serviceCode,
                    NO_COURT_VENUES_FOUND_FOR_SERVICE_CODE
                );
            return getLrdCourtVenueResponses(lrdCourtVenueResponse, courtVenueRequestParam);
        }
        if (isNotEmpty(courtTypeId)) {
            log.info("{} : Obtaining court venues for court type id: {}", loggingComponentName, courtTypeId);

            List<LrdCourtVenueResponse> lrdCourtVenueResponse =
                getAllCourtVenues(
                    () -> isCourtStatusFilterPresent(courtVenueRequestParam)
                        ? courtVenueRepository.findByCourtTypeId(courtTypeId.toString())
                        : courtVenueRepository.findByCourtTypeIdWithOpenCourtStatus(courtTypeId.toString()),
                    courtTypeId.toString(),
                    NO_COURT_VENUES_FOUND_FOR_COURT_TYPE_ID
                );
            return getLrdCourtVenueResponses(lrdCourtVenueResponse, courtVenueRequestParam);
        }
        if (isNotEmpty(regionId)) {
            log.info("{} : Obtaining court venues for region id: {}", loggingComponentName, regionId);
            List<LrdCourtVenueResponse> lrdCourtVenueResponse = getAllCourtVenues(
                () -> isCourtStatusFilterPresent(courtVenueRequestParam)
                    ? courtVenueRepository.findByRegionId(regionId.toString())
                    : courtVenueRepository.findByRegionIdWithOpenCourtStatus(regionId.toString()),
                regionId.toString(),
                NO_COURT_VENUES_FOUND_FOR_REGION_ID
            );
            return getLrdCourtVenueResponses(lrdCourtVenueResponse, courtVenueRequestParam);

        }
        if (isNotEmpty(clusterId)) {
            log.info("{} : Obtaining court venues for cluster id: {}", loggingComponentName, clusterId);
            List<LrdCourtVenueResponse> lrdCourtVenueResponse = getAllCourtVenues(
                () -> isCourtStatusFilterPresent(courtVenueRequestParam)
                    ? courtVenueRepository.findByClusterId(clusterId.toString())
                    : courtVenueRepository.findByClusterIdWithOpenCourtStatus(clusterId.toString()),
                clusterId.toString(),
                NO_COURT_VENUES_FOUND_FOR_CLUSTER_ID
            );
            return getLrdCourtVenueResponses(lrdCourtVenueResponse, courtVenueRequestParam);
        }
        if (isNotEmpty(courtVenueName)) {
            log.info("{} : Obtaining court venues for court venue name: {}", loggingComponentName, courtVenueName);
            List<LrdCourtVenueResponse> lrdCourtVenueResponse = getAllCourtVenues(
                () -> isCourtStatusFilterPresent(courtVenueRequestParam)
                    ? courtVenueRepository.findByCourtVenueNameOrSiteName(courtVenueName.strip())
                    : courtVenueRepository.findByCourtVenueNameOrSiteNameWithOpenCourtStatus(courtVenueName.strip()),
                courtVenueName,
                NO_COURT_VENUES_FOUND_FOR_COURT_VENUE_NAME
            );
            return getLrdCourtVenueResponses(lrdCourtVenueResponse, courtVenueRequestParam);
        }
        List<LrdCourtVenueResponse> initialResult =
            getAllCourtVenues(() -> isCourtStatusFilterPresent(courtVenueRequestParam)
                                  ? courtVenueRepository.findAllCourtVenues()
                                  : courtVenueRepository.findAllWithOpenCourtStatus(), null,
                              NO_COURT_VENUES_FOUND
            );
        return getLrdCourtVenueResponses(initialResult, courtVenueRequestParam);
    }

    private List<LrdCourtVenueV2Response> buildCourtVenueV2Responses(List<LrdCourtVenueResponse> legacyResponses) {
        List<String> mrdVenueIds = legacyResponses.stream()
            .map(LrdCourtVenueResponse::getMrdVenueId)
            .filter(StringUtils::isNotBlank)
            .map(String::toUpperCase)
            .distinct()
            .toList();

        if (mrdVenueIds.isEmpty()) {
            return List.of();
        }

        Map<String, CourtVenue> courtVenuesByMrdVenueId = courtVenueRepository
            .findByMrdVenueIdInWithOpenCourtStatus(mrdVenueIds)
            .stream()
            .collect(Collectors.toMap(
                courtVenue -> courtVenue.getMrdVenueId().toUpperCase(),
                courtVenue -> courtVenue,
                (first, second) -> first
            ));

        CourtVenueV2Lookups lookups = getCourtVenueV2Lookups(mrdVenueIds);

        List<LrdCourtVenueV2Response> responses = legacyResponses.stream()
            .map(LrdCourtVenueResponse::getMrdVenueId)
            .filter(StringUtils::isNotBlank)
            .map(mrdId -> courtVenuesByMrdVenueId.get(mrdId.toUpperCase()))
            .filter(Objects::nonNull)
            .map(courtVenue -> buildCourtVenueV2Response(courtVenue, lookups))
            .toList();

        if (responses.isEmpty()) {
            return List.of();
        }

        return responses;
    }

    private List<LrdCourtVenueV2Response> applyCourtUseFilters(List<LrdCourtVenueV2Response> responses,
                                                               CourtVenueRequestParam requestParam) {
        return responses.stream()
            .filter(response -> matchesFlag(
                requestParam.getIsHearingLocation(),
                response.getCourtUse().isHearingLocation()
            ))
            .filter(response -> matchesFlag(
                requestParam.getIsCaseManagementLocation(),
                response.getCourtUse().isCaseManagementLocation()
            ))
            .filter(response -> matchesFlag(
                requestParam.getIsTemporaryLocation(),
                response.getCourtUse().isTemporaryLocation()
            ))
            .filter(response -> matchesFlag(
                requestParam.getIsNightingaleCourt(),
                response.getCourtUse().isNightingaleCourt()
            ))
            .filter(response -> matchesFlag(
                requestParam.getIsDistrictRegistry(),
                response.getCourtUse().isDistrictRegistry()
            ))
            .filter(response -> matchesFlag(
                requestParam.getIsAppealCentre(),
                response.getCourtUse().isAppealCentre()
            ))
            .toList();
    }

    private boolean matchesFlag(String requestedValue, boolean actualValue) {
        if (StringUtils.isBlank(requestedValue)) {
            return true;
        }
        return VALUE_Y.equalsIgnoreCase(requestedValue) == actualValue;
    }

    private List<LrdCourtVenueResponse> retrieveCourtVenuesByEpimmsId(String epimmsId,
                                                                      CourtVenueRequestParam requestParam) {
        log.info("{} : Obtaining court venue for epimms id(s): {}", loggingComponentName, epimmsId);

        if (epimmsId.strip().equalsIgnoreCase(LocationRefConstants.ALL)) {
            return getAllCourtVenues(
                () -> isCourtStatusFilterPresent(requestParam)
                    ? courtVenueRepository.findAll()
                    : courtVenueRepository.findAllWithOpenCourtStatus(),
                null,
                NO_COURT_VENUES_FOUND
            );
        }
        List<String> epimsIdList = checkIfValidCsvIdentifiersAndReturnList(
            epimmsId,
            EXCEPTION_MSG_NO_VALID_EPIM_ID_PASSED
        );
        if (isListContainsTextIgnoreCase(epimsIdList, LocationRefConstants.ALL)) {
            return getAllCourtVenues(
                () -> isCourtStatusFilterPresent(requestParam)
                    ? courtVenueRepository.findAll()
                    : courtVenueRepository.findAllWithOpenCourtStatus(),
                null,
                NO_COURT_VENUES_FOUND
            );
        }
        checkForInvalidIdentifiersAndRemoveFromIdList(
            epimsIdList,
            ALPHA_NUMERIC_REGEX, log,
            loggingComponentName,
            EXCEPTION_MSG_NO_VALID_EPIM_ID_PASSED
        );

        List<CourtVenue> courtVenues = isCourtStatusFilterPresent(requestParam)
            ? courtVenueRepository.findByEpimmsIdIn(epimsIdList)
            : courtVenueRepository.findByEpimmsIdInWithOpenCourtStatus(epimsIdList);

        handleIfCourtVenuesEmpty(
            () -> isEmpty(courtVenues), NO_COURT_VENUES_FOUND_FOR_FOR_EPIMMS_ID, epimsIdList.toString()
        );

        return getCourtVenueListResponse(courtVenues);
    }

    private List<LrdCourtVenueResponse> retrieveCourtVenuesByMrdVenueId(String mrdVenueId,
                                                                        CourtVenueRequestParam requestParam) {
        log.info("{} : Obtaining court venue for mrd venue id: {}", loggingComponentName, mrdVenueId);

        String trimmedMrdVenueId = mrdVenueId.strip();
        if (!isRegexSatisfied(trimmedMrdVenueId, MRD_VENUE_ID_REGEX)) {
            throw new InvalidRequestException(
                String.format(EXCEPTION_MSG_NO_VALID_MRD_VENUE_ID_PASSED, mrdVenueId)
            );
        }

        List<CourtVenue> courtVenues = isCourtStatusFilterPresent(requestParam)
            ? courtVenueRepository.findByMrdVenueId(trimmedMrdVenueId)
            : courtVenueRepository.findByMrdVenueIdWithOpenCourtStatus(trimmedMrdVenueId);

        handleIfCourtVenuesEmpty(
            () -> isEmpty(courtVenues), NO_COURT_VENUES_FOUND_FOR_MRD_VENUE_ID, trimmedMrdVenueId
        );

        return getCourtVenueListResponse(courtVenues);
    }


    private List<LrdCourtVenueResponse> retrieveCourtVenuesByEpimmsIdAndCourtType(String epimmsId,
                                                                                  Integer courtTypeId,
                                                                                  String serviceCode,
                                                                                  CourtVenueRequestParam requestParam) {

        if (epimmsId.strip().equalsIgnoreCase(LocationRefConstants.ALL)) {
            throw new InvalidRequestException(String.format(EXCEPTION_MSG_NO_VALID_EPIM_ID_PASSED, epimmsId));
        }
        List<String> epimsIdList = checkIfValidCsvIdentifiersAndReturnList(
            epimmsId,
            EXCEPTION_MSG_NO_VALID_EPIM_ID_PASSED
        );
        if (isListContainsTextIgnoreCase(epimsIdList, LocationRefConstants.ALL)) {
            throw new InvalidRequestException(String.format(EXCEPTION_MSG_NO_VALID_EPIM_ID_PASSED, epimmsId));
        }
        checkForInvalidIdentifiersAndRemoveFromIdList(
            epimsIdList,
            ALPHA_NUMERIC_REGEX, log,
            loggingComponentName,
            EXCEPTION_MSG_NO_VALID_EPIM_ID_PASSED
        );

        String courtTypeIdValue = (courtTypeId != null) ? courtTypeId.toString() : null;
        String serviceCodeValue = isNotBlank(serviceCode) ? serviceCode.toUpperCase() : null;
        List<CourtVenue> courtVenues = isCourtStatusFilterPresent(requestParam)
            ? courtVenueRepository.findByCourtTypeIdServiceCodeAndEpimmsId(epimsIdList, courtTypeIdValue,
                                                                           serviceCodeValue)
            : courtVenueRepository.findByCourtTypeIdServiceCodeAndEpimmsIdWithOpenCourtStatus(
                epimsIdList,
                courtTypeIdValue,
                serviceCodeValue
            );
        handleIfCourtVenuesEmpty(
            () -> isEmpty(courtVenues), NO_COURT_VENUES_FOUND_FOR_FOR_EPIMMS_ID, epimsIdList.toString()
        );
        return getCourtVenueListResponse(courtVenues);
    }




    private List<LrdCourtVenueResponse> getAllCourtVenues(Supplier<List<CourtVenue>> courtVenueSupplier, String id,
        String noDataFoundMessage) {

        List<CourtVenue> courtVenues = courtVenueSupplier.get();
        handleIfCourtVenuesEmpty(() -> isEmpty(courtVenues), noDataFoundMessage, id);
        return getCourtVenueListResponse(courtVenues);
    }

    private List<LrdCourtVenueResponse> getCourtVenueListResponse(List<CourtVenue> courtVenues) {
        return courtVenues
            .stream()
            .map(LrdCourtVenueResponse::new)
            .toList();
    }

    private boolean isCourtStatusFilterPresent(CourtVenueRequestParam courtVenueRequestParam) {
        return courtVenueRequestParam != null && StringUtils.isNotBlank(courtVenueRequestParam.getCourtStatus());
    }

    private CourtVenueV2Lookups getCourtVenueV2Lookups(List<String> mrdVenueIds) {
        Map<String, List<LrdCourtVenueV2Response.Name>> namesByMrdVenueId = courtVenueRepository
            .findNamesByMrdVenueIdIn(mrdVenueIds)
            .stream()
            .collect(Collectors.groupingBy(
                projection -> projection.getMrdVenueId().toUpperCase(),
                Collectors.mapping(this::toNameResponse, Collectors.toList())
            ));

        Map<String, List<LrdCourtVenueV2Response.Address>> addressesByMrdVenueId = courtVenueRepository
            .findAddressesByMrdVenueIdIn(mrdVenueIds)
            .stream()
            .collect(Collectors.groupingBy(
                projection -> projection.getMrdVenueId().toUpperCase(),
                Collectors.mapping(this::toAddressResponse, Collectors.toList())
            ));

        Map<String, List<LrdCourtVenueV2Response.Contact>> contactsByMrdVenueId = courtVenueRepository
            .findContactsByMrdVenueIdIn(mrdVenueIds)
            .stream()
            .collect(Collectors.groupingBy(
                projection -> projection.getMrdVenueId().toUpperCase(),
                Collectors.mapping(this::toContactResponse, Collectors.toList())
            ));

        Map<String, Set<String>> usesByMrdVenueId = courtVenueRepository
            .findUsesByMrdVenueIdIn(mrdVenueIds)
            .stream()
            .collect(Collectors.groupingBy(
                projection -> projection.getMrdVenueId().toUpperCase(),
                Collectors.mapping(CourtVenueUseProjection::getUseType, Collectors.toSet())
            ));

        Map<String, Map<String, String>> urlsByMrdVenueId = courtVenueRepository
            .findUrlsByMrdVenueIdIn(mrdVenueIds)
            .stream()
            .collect(Collectors.groupingBy(
                projection -> projection.getMrdVenueId().toUpperCase(),
                Collectors.toMap(CourtVenueUrlProjection::getType, CourtVenueUrlProjection::getUrl,
                                 (first, second) -> first)
            ));

        Map<String, Map<String, String>> referenceCodesByMrdVenueId = courtVenueRepository
            .findReferenceCodesByMrdVenueIdIn(mrdVenueIds)
            .stream()
            .collect(Collectors.groupingBy(
                projection -> projection.getMrdVenueId().toUpperCase(),
                Collectors.toMap(CourtVenueReferenceCodeProjection::getType,
                                 CourtVenueReferenceCodeProjection::getValue,
                                 (first, second) -> first)
            ));

        return new CourtVenueV2Lookups(
            namesByMrdVenueId,
            addressesByMrdVenueId,
            contactsByMrdVenueId,
            usesByMrdVenueId,
            urlsByMrdVenueId,
            referenceCodesByMrdVenueId
        );
    }

    private LrdCourtVenueV2Response buildCourtVenueV2Response(CourtVenue courtVenue, CourtVenueV2Lookups lookups) {
        String mrdVenueId = courtVenue.getMrdVenueId();
        String mrdVenueIdKey = mrdVenueId.toUpperCase();
        Set<String> useTypes = lookups.usesByMrdVenueId().getOrDefault(mrdVenueIdKey, Set.of());
        Map<String, String> urls = lookups.urlsByMrdVenueId().getOrDefault(mrdVenueIdKey, Map.of());
        Map<String, String> referenceCodes = lookups.referenceCodesByMrdVenueId().getOrDefault(mrdVenueIdKey, Map.of());

        return LrdCourtVenueV2Response.builder()
            .mrdVenueId(mrdVenueId)
            .epimsId(courtVenue.getEpimmsId())
            .mrdBuildingId(courtVenue.getMrdBuildingLocationId())
            .serviceCode(courtVenue.getServiceCode())
            .courtStatus(getCourtStatus(courtVenue))
            .openDate(nonBlank(courtVenue.getOpenDate() == null ? null : courtVenue.getOpenDate().toString()))
            .closedDate(courtVenue.getClosedDate().map(date -> date.toLocalDate().toString()).orElse(null))
            .openForPublic(toYesNo(courtVenue.getOpenForPublic()))
            .dxAddress(courtVenue.getDxAddress())
            .region(courtVenue.getRegionId())
            .cluster(courtVenue.getClusterId())
            .locationType(courtVenue.getLocationType())
            .courtUse(buildCourtUse(courtVenue, useTypes))
            .parentLocation(courtVenue.getParentLocation())
            .parentVenueId(courtVenue.getParentId())
            .districtRegistryVenueId(courtVenue.getDistrictRegistryVenueId())
            .appealCentreVenueId(courtVenue.getAppealCentreVenueId())
            .venueOuCode(referenceCodes.getOrDefault(REFERENCE_CODE_VENUE_OU_CODE, courtVenue.getVenueOuCode()))
            .serviceUrl(urls.getOrDefault(URL_TYPE_SERVICE, courtVenue.getServiceUrl()))
            .factUrl(urls.getOrDefault(URL_TYPE_FACT, courtVenue.getFactUrl()))
            .names(lookups.namesByMrdVenueId().getOrDefault(mrdVenueIdKey, List.of()))
            .addresses(lookups.addressesByMrdVenueId().getOrDefault(mrdVenueIdKey, List.of()))
            .contacts(lookups.contactsByMrdVenueId().getOrDefault(mrdVenueIdKey, List.of()))
            .build();
    }

    private LrdCourtVenueV2Response.CourtUse buildCourtUse(CourtVenue courtVenue, Set<String> useTypes) {
        return LrdCourtVenueV2Response.CourtUse.builder()
            .hearingLocation(isUseEnabled(useTypes, USE_HEARING, courtVenue.getIsHearingLocation()))
            .caseManagementLocation(isUseEnabled(useTypes, USE_CASE_MANAGEMENT,
                                                 courtVenue.getIsCaseManagementLocation()))
            .districtRegistry(isUseEnabled(useTypes, USE_DISTRICT_REGISTRY, courtVenue.getIsDistrictRegistry()))
            .temporaryLocation(isUseEnabled(useTypes, USE_TEMPORARY, courtVenue.getIsTemporaryLocation()))
            .nightingaleCourt(isUseEnabled(useTypes, USE_NIGHTINGALE, courtVenue.getIsNightingaleCourt()))
            .appealCentre(isUseEnabled(useTypes, USE_APPEAL_CENTRE, courtVenue.getIsAppealCentre()))
            .build();
    }

    private boolean isUseEnabled(Set<String> useTypes, String useType, String fallbackFlag) {
        return useTypes.contains(useType) || VALUE_Y.equalsIgnoreCase(fallbackFlag);
    }

    private String getCourtStatus(CourtVenue courtVenue) {
        if (StringUtils.isNotBlank(courtVenue.getCourtStatusCode())) {
            return courtVenue.getCourtStatusCode();
        }
        return courtVenue.getCourtStatus();
    }

    private String toYesNo(Boolean value) {
        if (value == null) {
            return null;
        }
        return Boolean.TRUE.equals(value) ? VALUE_Y : VALUE_N;
    }

    private String nonBlank(String value) {
        return StringUtils.isBlank(value) ? null : value;
    }

    private LrdCourtVenueV2Response.Name toNameResponse(CourtVenueNameProjection projection) {
        return LrdCourtVenueV2Response.Name.builder()
            .type(projection.getType())
            .language(projection.getLanguage())
            .name(projection.getName())
            .build();
    }

    private LrdCourtVenueV2Response.Address toAddressResponse(CourtVenueAddressProjection projection) {
        return LrdCourtVenueV2Response.Address.builder()
            .type(projection.getType())
            .address(projection.getAddress())
            .postCode(projection.getPostCode())
            .uprn(projection.getUprn())
            .language(projection.getLanguage())
            .build();
    }

    private LrdCourtVenueV2Response.Contact toContactResponse(CourtVenueContactProjection projection) {
        return LrdCourtVenueV2Response.Contact.builder()
            .method(projection.getMethod())
            .type(projection.getType())
            .value(projection.getValue())
            .build();
    }

    private record CourtVenueV2Lookups(
        Map<String, List<LrdCourtVenueV2Response.Name>> namesByMrdVenueId,
        Map<String, List<LrdCourtVenueV2Response.Address>> addressesByMrdVenueId,
        Map<String, List<LrdCourtVenueV2Response.Contact>> contactsByMrdVenueId,
        Map<String, Set<String>> usesByMrdVenueId,
        Map<String, Map<String, String>> urlsByMrdVenueId,
        Map<String, Map<String, String>> referenceCodesByMrdVenueId
    ) {
    }

    private void handleIfCourtVenuesEmpty(BooleanSupplier courtVenueResponseVerifier,
                                          String noDataFoundMessage,
                                          String id) {
        if (courtVenueResponseVerifier.getAsBoolean()) {
            noDataFoundMessage = (isNotBlank(id)) ? String.format(noDataFoundMessage, id) : noDataFoundMessage;
            log.error("{} : {}", loggingComponentName, noDataFoundMessage);
            throw new ResourceNotFoundException(noDataFoundMessage);
        }
    }

    private List<LrdCourtVenueResponse> getLrdCourtVenueResponses(
        List<LrdCourtVenueResponse> inputLrdCourtVenueResponse,
        CourtVenueRequestParam courtVenueRequestParam) {

        List<LrdCourtVenueResponse> result = applyAdditionalFilters(
            inputLrdCourtVenueResponse,
            courtVenueRequestParam
        );
        if (isEmpty(result)) {
            throw new ResourceNotFoundException(NO_COURT_VENUES_FOUND);
        }
        return result;
    }

    private List<LrdCourtVenueResponse> applyAdditionalFilters(List<LrdCourtVenueResponse> inputLrdCourtVenueResponse,
        CourtVenueRequestParam courtVenueRequestParam) {

        List<Predicate<LrdCourtVenueResponse>> allPredicates = getPredicates(courtVenueRequestParam);


        return inputLrdCourtVenueResponse.stream()
            .filter(allPredicates.stream().reduce(x -> true, Predicate::and))
            .toList();

    }

    private List<Predicate<LrdCourtVenueResponse>> getPredicates(CourtVenueRequestParam courtVenueRequestParam) {

        List<Predicate<LrdCourtVenueResponse>> allPredicates = new ArrayList<>();

        String isHearingLocationValue = courtVenueRequestParam.getIsHearingLocation();

        if (IS_HEARING_LOCATION_Y.equalsIgnoreCase(isHearingLocationValue)
            || IS_HEARING_LOCATION_N.equalsIgnoreCase(isHearingLocationValue)) {
            allPredicates.add(
                courtVenue -> isHearingLocationValue.equalsIgnoreCase(courtVenue.getIsHearingLocation())
            );

        }

        String isCaseMgntLocationValue = courtVenueRequestParam.getIsCaseManagementLocation();

        if (IS_CASE_MANAGEMENT_LOCATION_Y.equalsIgnoreCase(isCaseMgntLocationValue)
            || IS_CASE_MANAGEMENT_LOCATION_N.equalsIgnoreCase(isCaseMgntLocationValue)) {
            allPredicates.add(
                courtVenue -> isCaseMgntLocationValue.equalsIgnoreCase(courtVenue.getIsCaseManagementLocation())
            );

        }

        String isTemporaryLocationValue = courtVenueRequestParam.getIsTemporaryLocation();

        if (IS_TEMPORARY_LOCATION_Y.equalsIgnoreCase(isTemporaryLocationValue)
            || IS_TEMPORARY_LOCATION_N.equalsIgnoreCase(isTemporaryLocationValue)) {
            allPredicates.add(
                courtVenue -> isTemporaryLocationValue.equalsIgnoreCase(courtVenue.getIsTemporaryLocation())
            );

        }

        String isLocationTypeValue = courtVenueRequestParam.getLocationType();

        if (StringUtils.isNotBlank(isLocationTypeValue)) {
            allPredicates.add(
                courtVenue -> isLocationTypeValue.equalsIgnoreCase(courtVenue.getLocationType())
            );

        }

        String mrdBuildingIdValue = courtVenueRequestParam.getMrdBuildingId();

        if (StringUtils.isNotBlank(mrdBuildingIdValue)) {
            allPredicates.add(
                courtVenue -> mrdBuildingIdValue.equalsIgnoreCase(courtVenue.getMrdBuildingLocationId())
            );
        }

        String courtStatusValue = courtVenueRequestParam.getCourtStatus();

        if (COURT_STATUS_OPEN.equalsIgnoreCase(courtStatusValue)
            || COURT_STATUS_CLOSED.equalsIgnoreCase(courtStatusValue)) {
            allPredicates.add(
                courtVenue -> courtStatusValue.equalsIgnoreCase(courtVenue.getCourtStatus())
            );
        }
        return allPredicates;
    }
}
