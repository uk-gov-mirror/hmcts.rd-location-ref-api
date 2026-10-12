package uk.gov.hmcts.reform.lrdapi.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.lrdapi.controllers.advice.InvalidRequestException;
import uk.gov.hmcts.reform.lrdapi.controllers.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdCourtVenueResponse;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdCourtVenueV2Response;
import uk.gov.hmcts.reform.lrdapi.controllers.response.LrdCourtVenuesByServiceCodeResponse;
import uk.gov.hmcts.reform.lrdapi.domain.Cluster;
import uk.gov.hmcts.reform.lrdapi.domain.CourtType;
import uk.gov.hmcts.reform.lrdapi.domain.CourtVenue;
import uk.gov.hmcts.reform.lrdapi.domain.CourtVenueRequestParam;
import uk.gov.hmcts.reform.lrdapi.domain.Region;
import uk.gov.hmcts.reform.lrdapi.repository.CourtTypeServiceAssocRepository;
import uk.gov.hmcts.reform.lrdapi.repository.CourtVenueRepository;
import uk.gov.hmcts.reform.lrdapi.repository.projection.CourtVenueUseProjection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.lrdapi.controllers.constants.LocationRefConstants.EXCEPTION_MSG_SERVICE_CODE_SPCL_CHAR;

@ExtendWith(MockitoExtension.class)
class CourtVenueServiceImplTest {

    @InjectMocks
    CourtVenueServiceImpl courtVenueService;

    @Mock
    CourtTypeServiceAssocRepository courtTypeServiceAssocRepository;
    @Mock
    CourtVenueRepository courtVenueRepository;

    private CourtVenueRequestParam courtVenueRequestParam;

    @BeforeEach
    public void setup() {
        courtVenueRequestParam =
            new CourtVenueRequestParam();

    }

    @Test
    void testValidateServiceCode_ValidValueWithSpaces_ReturnsTrimmedValue() {
        assertEquals("ABC1", CourtVenueServiceImpl.validateServiceCode(" ABC1 "));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void testValidateServiceCode_BlankValue_ThrowsInvalidRequestException(String serviceCode) {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class,
            () -> CourtVenueServiceImpl.validateServiceCode(serviceCode)
        );

        assertEquals("No service code provided", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"@AB_C", " @AB_C ", "%$^$%^$%"})
    void testValidateServiceCode_InvalidValue_ThrowsInvalidRequestException(String serviceCode) {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class,
            () -> CourtVenueServiceImpl.validateServiceCode(serviceCode)
        );

        assertEquals(EXCEPTION_MSG_SERVICE_CODE_SPCL_CHAR, exception.getMessage());
    }

    @Test
    void testRetrieveCourtVenuesByServiceCode() {
        CourtType courtType = CourtType.builder()
            .typeOfCourt("courtType")
            .courtTypeId("10")
            .build();

        CourtVenue courtVenue = CourtVenue.builder()
            .courtVenueId(1L)
            .courtType(courtType)
            .openForPublic(Boolean.TRUE)
            .welshVenueName("testVenue")
            .isTemporaryLocation("N")
            .isNightingaleCourt("N")
            .locationType("Court")
            .parentLocation("366559")
            .welshCourtName("welshCourtName1")
            .uprn("1234")
            .venueOuCode("87675")
            .mrdBuildingLocationId("8686")
            .mrdVenueId("765")
            .serviceUrl("https://serviceurl.com")
            .factUrl("https://facturl.com")
            .serviceCode("ABC1")
            .build();

        List<CourtVenue> courtVenues = Collections.singletonList(courtVenue);

        when(courtVenueRepository.findByServiceCodeWithOpenCourtStatus(anyString())).thenReturn(courtVenues);

        LrdCourtVenuesByServiceCodeResponse response
            = courtVenueService.retrieveCourtVenuesByServiceCode("ABC1");

        assertNotNull(response);
        assertEquals("ABC1", response.getServiceCode());
        assertEquals(courtType.getCourtTypeId(), response.getCourtTypeId());
        assertEquals(courtType.getTypeOfCourt(), response.getCourtType());
        assertNotNull(response.getCourtVenues());
        assertNull(response.getWelshCourtType());
        assertEquals("testVenue",response.getCourtVenues().get(0).getWelshVenueName());
        assertEquals("N",response.getCourtVenues().get(0).getIsTemporaryLocation());
        assertEquals("N",response.getCourtVenues().get(0).getIsNightingaleCourt());
        assertEquals("Court",response.getCourtVenues().get(0).getLocationType());
        assertEquals("366559",response.getCourtVenues().get(0).getParentLocation());
        assertEquals("welshCourtName1",response.getCourtVenues().get(0).getWelshCourtName());
        assertEquals("1234",response.getCourtVenues().get(0).getUprn());
        assertEquals("87675",response.getCourtVenues().get(0).getVenueOuCode());
        assertEquals("8686",response.getCourtVenues().get(0).getMrdBuildingLocationId());
        assertEquals("765",response.getCourtVenues().get(0).getMrdVenueId());
        assertEquals("https://serviceurl.com",response.getCourtVenues().get(0).getServiceUrl());
        assertEquals("https://facturl.com",response.getCourtVenues().get(0).getFactUrl());
        assertEquals("ABC1",response.getCourtVenues().get(0).getServiceCode());

        verify(courtVenueRepository, times(1)).findByServiceCodeWithOpenCourtStatus("ABC1");
    }

    @Test
    void testRetrieveCourtVenuesByServiceCode_WithInvalidServiceCode() {
        when(courtVenueRepository.findByServiceCodeWithOpenCourtStatus(anyString())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> courtVenueService.retrieveCourtVenuesByServiceCode("ABC1"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void testRetrieveCourtVenuesByServiceCode_WithBlankServiceCode(String serviceCode) {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class,
            () -> courtVenueService.retrieveCourtVenuesByServiceCode(serviceCode)
        );

        assertEquals("No service code provided", exception.getMessage());
        verifyNoInteractions(courtVenueRepository);
    }

    @Test
    void testRetrieveCourtVenuesByServiceCode_WithNoCourtVenues() {

        when(courtVenueRepository.findByServiceCodeWithOpenCourtStatus(anyString())).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> courtVenueService.retrieveCourtVenuesByServiceCode("ABC1"));
    }

    @Test
    void test_RetrieveCourtVenuesByEpimsIDs_OneIdPassed() {

        when(courtVenueRepository.findByEpimmsIdInWithOpenCourtStatus(anyList())).thenReturn(prepareCourtVenue());

        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("123", null, null, null, null, null,
                        false, courtVenueRequestParam);

        LrdCourtVenueResponse courtVenueResponse = courtVenueResponses.get(0);

        verifySingleResponse(courtVenueResponse);
    }

    @Test
    void test_RetrieveBuildingLocationsByEpimsIDs_MultipleIdsPassed() {

        when(courtVenueRepository.findByEpimmsIdInWithOpenCourtStatus(anyList()))
            .thenReturn(prepareMultiCourtVenueResponse());
        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("123,1234", null, null, null, null, null,
                        false, courtVenueRequestParam);
        verifyMultiResponse(courtVenueResponses);
    }

    @Test
    void testGetAllCourtVenues_EpimmsIdAll() {
        when(courtVenueRepository.findAllWithOpenCourtStatus())
            .thenReturn(prepareMultiCourtVenueResponse());
        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("All", null, null, null, null, null,
                                           false, courtVenueRequestParam);
        verifyMultiResponse(courtVenueResponses);
    }

    @Test
    void testGetAllCourtVenues_EpimmsIdAllWithMultipleIds() {
        when(courtVenueRepository.findAllWithOpenCourtStatus())
            .thenReturn(prepareMultiCourtVenueResponse());
        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("All,123", null, null, null, null, null,
                                           false, courtVenueRequestParam);
        verifyMultiResponse(courtVenueResponses);
    }

    @Test
    void testRetrieveCourtVenuesFiltersByMrdBuildingId() {
        List<CourtVenue> courtVenues = List.of(
            CourtVenue.builder()
                .courtVenueId(5L)
                .epimmsId("123")
                .courtType(getCourtType())
                .openForPublic(true)
                .mrdBuildingLocationId("MRD-BLD-38")
                .mrdVenueId("MRD-000123")
                .build(),
            CourtVenue.builder()
                .courtVenueId(6L)
                .epimmsId("456")
                .courtType(getCourtType())
                .openForPublic(true)
                .mrdBuildingLocationId("MRD-BLD-3851")
                .mrdVenueId("MRD-000456")
                .build()
        );
        courtVenueRequestParam.setMrdBuildingId("MRD-BLD-38");
        when(courtVenueRepository.findAllWithOpenCourtStatus()).thenReturn(courtVenues);

        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails(null, null, null, null, null, null,
                                           false, courtVenueRequestParam);

        assertThat(courtVenueResponses).hasSize(1);
        assertThat(courtVenueResponses.get(0).getMrdBuildingLocationId()).isEqualTo("MRD-BLD-38");
    }

    @Test
    void testRetrieveCourtVenuesFiltersByClosedCourtStatus() {
        List<CourtVenue> courtVenues = List.of(
            CourtVenue.builder()
                .courtVenueId(5L)
                .epimmsId("123")
                .courtType(getCourtType())
                .openForPublic(true)
                .courtStatus("Open")
                .mrdVenueId("MRD-000123")
                .build(),
            CourtVenue.builder()
                .courtVenueId(6L)
                .epimmsId("456")
                .courtType(getCourtType())
                .openForPublic(true)
                .courtStatus("Closed")
                .mrdVenueId("MRD-000456")
                .build()
        );
        courtVenueRequestParam.setCourtStatus("Closed");
        when(courtVenueRepository.findAllCourtVenues()).thenReturn(courtVenues);

        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails(null, null, null, null, null, null,
                                           false, courtVenueRequestParam);

        assertThat(courtVenueResponses).hasSize(1);
        assertThat(courtVenueResponses.get(0).getCourtStatus()).isEqualTo("Closed");
        verify(courtVenueRepository, times(1)).findAllCourtVenues();
        verify(courtVenueRepository, times(0)).findAllWithOpenCourtStatus();
    }


    @Test
    void test_RetrieveCourtVenuesByEpimsIDsAndCourtTypeID_OneIdPassed() {

        when(courtVenueRepository.findByCourtTypeIdServiceCodeAndEpimmsIdWithOpenCourtStatus(anyList(), anyString(),
                                                                                  nullable(String.class)))
            .thenReturn(prepareCourtVenue());

        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("123", 123, null, null, null, null,
                                           true, courtVenueRequestParam);

        LrdCourtVenueResponse courtVenueResponse = courtVenueResponses.get(0);

        verifySingleResponse(courtVenueResponse);
    }

    @Test
    void test_RetrieveCourtVenuesByEpimsIDsAndCourtType_MultipleIdsPassed() {

        when(courtVenueRepository.findByCourtTypeIdServiceCodeAndEpimmsIdWithOpenCourtStatus(anyList(), anyString(),
                                                                                  nullable(String.class)))
            .thenReturn(prepareMultiCourtVenueResponse());
        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("123,1234", 123, null, null, null, null,
                                           true, courtVenueRequestParam);
        verifyMultiResponse(courtVenueResponses);
    }

    @Test
    void test_RetrieveCourtVenuesByEpimsIdAndServiceCode_UppercasesServiceCode() {
        when(courtVenueRepository.findByCourtTypeIdServiceCodeAndEpimmsIdWithOpenCourtStatus(anyList(),
                                                                                             isNull(),
                                                                                             anyString()))
            .thenReturn(prepareCourtVenue());

        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("123", null, "abC1", null, null, null,
                                           true, courtVenueRequestParam);

        verify(courtVenueRepository, times(1))
            .findByCourtTypeIdServiceCodeAndEpimmsIdWithOpenCourtStatus(anyList(), isNull(), eq("ABC1"));
        verifySingleResponse(courtVenueResponses.get(0));
    }

    @Test
    void test_RetrieveCourtVenuesByEpimsIdCourtTypeAndServiceCode_UppercasesServiceCode() {
        when(courtVenueRepository.findByCourtTypeIdServiceCodeAndEpimmsIdWithOpenCourtStatus(anyList(),
                                                                                             anyString(),
                                                                                             anyString()))
            .thenReturn(prepareCourtVenue());

        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("123", 123, "abC1", null, null, null,
                                           true, courtVenueRequestParam);

        verify(courtVenueRepository, times(1))
            .findByCourtTypeIdServiceCodeAndEpimmsIdWithOpenCourtStatus(anyList(), eq("123"), eq("ABC1"));
        verifySingleResponse(courtVenueResponses.get(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"All","All,123"})
    void testGetAllCourtVenuesEpimmsIdAllAndCourtTyepId(String epimmsIds) {
        assertThrows(InvalidRequestException.class, () ->
            courtVenueService.retrieveCourtVenueDetails(epimmsIds, 123, null,
                                                        null, null, null, true, courtVenueRequestParam));
    }

    @Test
    void testGetAllCourtVenues() {
        when(courtVenueRepository.findAllWithOpenCourtStatus())
            .thenReturn(prepareCourtVenue());
        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("", null, null, null, null, null,
                                           false, courtVenueRequestParam);
        verifySingleResponse(courtVenueResponses.get(0));
    }

    @Test
    void test_RetrieveCourtVenuesByRegionId() {

        when(courtVenueRepository.findByRegionIdWithOpenCourtStatus(anyString())).thenReturn(prepareCourtVenue());

        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("", null, null, 1, null, null,
                                           false, courtVenueRequestParam);

        LrdCourtVenueResponse courtVenueResponse = courtVenueResponses.get(0);

        verifySingleResponse(courtVenueResponse);
    }

    @Test
    void test_RetrieveCourtVenuesByClusterId() {

        when(courtVenueRepository.findByClusterIdWithOpenCourtStatus(anyString())).thenReturn(prepareCourtVenue());

        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("", null, null, null, 1, null,
                                           false, courtVenueRequestParam);

        LrdCourtVenueResponse courtVenueResponse = courtVenueResponses.get(0);

        verifySingleResponse(courtVenueResponse);
    }

    @Test
    void test_RetrieveCourtVenuesByCourtTypeId() {

        when(courtVenueRepository.findByCourtTypeIdWithOpenCourtStatus(anyString())).thenReturn(prepareCourtVenue());

        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("", 1, null, null, null, null,
                                           false, courtVenueRequestParam);

        LrdCourtVenueResponse courtVenueResponse = courtVenueResponses.get(0);

        verifySingleResponse(courtVenueResponse);
    }

    @Test
    void test_RetrieveCourtVenuesByServiceCode() {

        when(courtVenueRepository.findByServiceCodeWithOpenCourtStatus(anyString())).thenReturn(prepareCourtVenue());

        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("", null, "AAA6", null, null, null,
                                           false, courtVenueRequestParam);

        LrdCourtVenueResponse courtVenueResponse = courtVenueResponses.get(0);

        verifySingleResponse(courtVenueResponse);
        verify(courtVenueRepository, times(1)).findByServiceCodeWithOpenCourtStatus("AAA6");
        verify(courtVenueRepository, times(0)).findByEpimmsIdInWithOpenCourtStatus(anyList());
        verify(courtVenueRepository, times(0)).findByRegionIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findByClusterIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findAll();
        verify(courtVenueRepository, times(0)).findAllWithOpenCourtStatus();
        verify(courtVenueRepository, times(0)).findByCourtTypeIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findByCourtVenueNameOrSiteNameWithOpenCourtStatus(anyString());
    }

    @Test
    void test_RetrieveCourtVenuesByServiceCode_WhenCourtTypeAlsoProvided() {

        when(courtVenueRepository.findByServiceCodeWithOpenCourtStatus(anyString())).thenReturn(prepareCourtVenue());

        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("", 17, "AAA6", null, null, null,
                                           false, courtVenueRequestParam);

        LrdCourtVenueResponse courtVenueResponse = courtVenueResponses.get(0);

        verifySingleResponse(courtVenueResponse);
        verify(courtVenueRepository, times(1)).findByServiceCodeWithOpenCourtStatus("AAA6");
        verify(courtVenueRepository, times(0)).findByEpimmsIdInWithOpenCourtStatus(anyList());
        verify(courtVenueRepository, times(0)).findByRegionIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findByClusterIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findAll();
        verify(courtVenueRepository, times(0)).findAllWithOpenCourtStatus();
        verify(courtVenueRepository, times(0)).findByCourtTypeIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findByCourtVenueNameOrSiteNameWithOpenCourtStatus(anyString());
    }

    @Test
    void test_RetrieveCourtVenuesByCourtVenueName() {

        when(courtVenueRepository.findByCourtVenueNameOrSiteNameWithOpenCourtStatus(anyString()))
            .thenReturn(prepareCourtVenue());

        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenueDetails("", null, null, null, null, "Court ABC",
                                           false, courtVenueRequestParam);

        LrdCourtVenueResponse courtVenueResponse = courtVenueResponses.get(0);

        verifySingleResponse(courtVenueResponse);
    }

    @Test
    void test_RetrieveCourtVenuesByEpimmsId_NotFound() {
        when(courtVenueRepository.findByEpimmsIdInWithOpenCourtStatus(anyList())).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> courtVenueService
            .retrieveCourtVenueDetails("123", null, null, null, null, null,
                                       false, courtVenueRequestParam));

        verify(courtVenueRepository, times(1)).findByEpimmsIdInWithOpenCourtStatus(anyList());
        verify(courtVenueRepository, times(0)).findByRegionIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0))
            .findByClusterIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findAll();
        verify(courtVenueRepository, times(0)).findByCourtTypeIdWithOpenCourtStatus(anyString());
    }

    @Test
    void test_RetrieveCourtVenuesByEpimmsId_InvalidList() {
        assertThrows(InvalidRequestException.class, () -> courtVenueService
            .retrieveCourtVenueDetails("{123}", null, null, null, null, null,
                                       false, courtVenueRequestParam));

        verify(courtVenueRepository, times(0)).findByEpimmsIdInWithOpenCourtStatus(anyList());
        verify(courtVenueRepository, times(0)).findByRegionIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0))
            .findByClusterIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findAll();
        verify(courtVenueRepository, times(0)).findByCourtTypeIdWithOpenCourtStatus(anyString());
    }

    @Test
    void test_RetrieveCourtVenuesByClusterId_NotFound() {
        when(courtVenueRepository.findByClusterIdWithOpenCourtStatus(anyString())).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> courtVenueService
            .retrieveCourtVenueDetails("", null, null, null, 1, null,
                                       false, courtVenueRequestParam));

        verify(courtVenueRepository, times(0)).findByEpimmsIdInWithOpenCourtStatus(anyList());
        verify(courtVenueRepository, times(0)).findByRegionIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(1))
            .findByClusterIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findAll();
        verify(courtVenueRepository, times(0)).findByCourtTypeIdWithOpenCourtStatus(anyString());
    }

    @Test
    void test_RetrieveCourtVenuesByRegionId_NotFound() {
        when(courtVenueRepository.findByRegionIdWithOpenCourtStatus(anyString())).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> courtVenueService
            .retrieveCourtVenueDetails("", null, null, 1, null, null,
                                       false, courtVenueRequestParam));

        verify(courtVenueRepository, times(0)).findByEpimmsIdInWithOpenCourtStatus(anyList());
        verify(courtVenueRepository, times(1)).findByRegionIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0))
            .findByClusterIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findAll();
        verify(courtVenueRepository, times(0)).findByCourtTypeIdWithOpenCourtStatus(anyString());
    }

    @Test
    void test_RetrieveCourtVenuesByCourtTypeId_NotFound() {
        when(courtVenueRepository.findByCourtTypeIdWithOpenCourtStatus(anyString())).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> courtVenueService
            .retrieveCourtVenueDetails("", 1, null, null, null, null,
                                       false, courtVenueRequestParam));

        verify(courtVenueRepository, times(0)).findByEpimmsIdInWithOpenCourtStatus(anyList());
        verify(courtVenueRepository, times(0)).findByRegionIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0))
            .findByClusterIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findAll();
        verify(courtVenueRepository, times(0)).findAllWithOpenCourtStatus();
        verify(courtVenueRepository, times(1)).findByCourtTypeIdWithOpenCourtStatus(anyString());
    }

    @Test
    void test_RetrieveCourtVenuesByServiceCode_NotFound() {
        when(courtVenueRepository.findByServiceCodeWithOpenCourtStatus(anyString())).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> courtVenueService
            .retrieveCourtVenueDetails("", null, "AAA6", null, null, null,
                                       false, courtVenueRequestParam));

        verify(courtVenueRepository, times(0)).findByEpimmsIdInWithOpenCourtStatus(anyList());
        verify(courtVenueRepository, times(0)).findByRegionIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findByClusterIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findAll();
        verify(courtVenueRepository, times(0)).findAllWithOpenCourtStatus();
        verify(courtVenueRepository, times(0)).findByCourtTypeIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(1)).findByServiceCodeWithOpenCourtStatus("AAA6");
        verify(courtVenueRepository, times(0)).findByCourtVenueNameOrSiteNameWithOpenCourtStatus(anyString());
    }

    @Test
    void test_RetrieveCourtVenuesByCourtVenueName_NotFound() {
        assertThrows(ResourceNotFoundException.class, () -> courtVenueService
            .retrieveCourtVenueDetails("", null, null, null, null, "test-name",
                                       false, courtVenueRequestParam));

        verify(courtVenueRepository, times(0)).findByEpimmsIdInWithOpenCourtStatus(anyList());
        verify(courtVenueRepository, times(0)).findByRegionIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0))
            .findByClusterIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(0)).findAll();
        verify(courtVenueRepository, times(0)).findAllWithOpenCourtStatus();
        verify(courtVenueRepository, times(0)).findByCourtTypeIdWithOpenCourtStatus(anyString());
        verify(courtVenueRepository, times(1)).findByCourtVenueNameOrSiteNameWithOpenCourtStatus(anyString());
    }

    @Test
    void test_RetrieveCourtVenuesBySearchString() {
        when(courtVenueRepository.findBySearchStringAndCourtTypeId(anyString(),
                                                                   anyList(),
                                                                   anyList(),
                                                                   anyString(),
                                                                   anyString(),
                                                                   anyString(),
                                                                   anyString())).thenReturn(prepareCourtVenue());
        var param = new CourtVenueRequestParam();
        param.setIsHearingLocation("Y");
        param.setLocationType("test");
        param.setIsCaseManagementLocation("Y");
        param.setIsTemporaryLocation("Y");

        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenuesBySearchString("ABC", "1,2","AAA2", param);

        LrdCourtVenueResponse courtVenueResponse = courtVenueResponses.get(0);

        verifySingleResponse(courtVenueResponse);
        verify(courtVenueRepository, times(1)).findBySearchStringAndCourtTypeId("ABC",
                                                                                List.of("1", "2"),
                                                                                List.of("AAA2"),
                                                                                "Y",
                                                                                "Y",
                                                                                "TEST",
                                                                                "Y");
    }

    @Test
    void test_GetCourtVenuesBySearchStringWhenCourtTypeIdIsNull() {
        when(courtVenueRepository.findBySearchStringAndCourtTypeId("ABC",
                                                                   null,
                                                                   null,
                                                                   null,
                                                                   null,
                                                                   null,
                                                                   null)).thenReturn(prepareCourtVenue());
        var param = new CourtVenueRequestParam();
        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenuesBySearchString("ABC", null, null,param);

        LrdCourtVenueResponse courtVenueResponse = courtVenueResponses.get(0);

        verifySingleResponse(courtVenueResponse);
    }

    @Test
    void test_GetCourtVenuesBySearchStringWhenInvalidHearingLocation() {
        var param = new CourtVenueRequestParam();
        param.setIsHearingLocation("YN");
        assertThrows(InvalidRequestException.class, () ->
            courtVenueService
                .retrieveCourtVenuesBySearchString("ABC", null,null, param));
    }

    @Test
    void test_GetCourtVenuesBySearchString_NotFound() {
        when(courtVenueRepository.findBySearchStringAndCourtTypeId("ABC",
                                                                   null,
                                                                   null,
                                                                   null,
                                                                   null,
                                                                   null,
                                                                   null)).thenReturn(new ArrayList<>());
        var param = new CourtVenueRequestParam();
        List<LrdCourtVenueResponse> courtVenueResponses =
            courtVenueService
                .retrieveCourtVenuesBySearchString("ABC", null,null, param);
        assertEquals(0,courtVenueResponses.size());
        verify(courtVenueRepository, times(1)).findBySearchStringAndCourtTypeId("ABC",
                                                                                null,
                                                                                null,
                                                                                null,
                                                                                null,
                                                                                null,
                                                                                null);
    }

    @Test
    void test_RetrieveCourtVenuesBySearchStringV2() {
        when(courtVenueRepository.findBySearchStringAndCourtTypeId("ABC",
                                                                   List.of("1", "2"),
                                                                   List.of("AAA2"),
                                                                   null,
                                                                   null,
                                                                   "COURT",
                                                                   null)).thenReturn(prepareCourtVenue());
        when(courtVenueRepository.findByMrdVenueIdInWithOpenCourtStatus(List.of("765")))
            .thenReturn(prepareCourtVenue());
        stubV2LookupsWithUses(
            courtVenueUseProjection("765", "CASE_MANAGEMENT"),
            courtVenueUseProjection("765", "HEARING")
        );
        var param = CourtVenueRequestParam.builder()
            .isHearingLocation("Y")
            .isCaseManagementLocation("Y")
            .locationType("Court")
            .build();

        List<LrdCourtVenueV2Response> courtVenueResponses =
            courtVenueService.retrieveCourtVenuesBySearchStringV2("ABC", "1,2", "aaa2", param);

        assertThat(courtVenueResponses).hasSize(1);
        assertThat(courtVenueResponses.get(0).getMrdVenueId()).isEqualTo("765");
        assertThat(courtVenueResponses.get(0).getServiceCode()).isEqualTo("AAA2");
        assertThat(courtVenueResponses.get(0).getCourtUse().isHearingLocation()).isTrue();
        assertThat(courtVenueResponses.get(0).getCourtUse().isCaseManagementLocation()).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
        "is_hearing_location,P",
        "is_case_management_location,P",
        "is_temporary_location,P",
        "is_nightingale_court,P",
        "is_district_registry,P",
        "is_appeal_centre,P"
    })
    void test_RetrieveCourtVenuesBySearchStringV2WithInvalidAdditionalFilter(String filterName, String filterValue) {
        var param = buildRequestParamForFilter(filterName, filterValue);

        assertThrows(InvalidRequestException.class, () ->
            courtVenueService.retrieveCourtVenuesBySearchStringV2("ABC", null, null, param));
    }

    @Test
    void test_RetrieveCourtVenuesBySearchStringV2WithNoResultsReturnsEmptyList() {
        when(courtVenueRepository.findBySearchStringAndCourtTypeId("ABC",
                                                                   null,
                                                                   null,
                                                                   null,
                                                                   null,
                                                                   null,
                                                                   null)).thenReturn(List.of());

        List<LrdCourtVenueV2Response> courtVenueResponses =
            courtVenueService.retrieveCourtVenuesBySearchStringV2("ABC", null, null, new CourtVenueRequestParam());

        assertThat(courtVenueResponses).isEmpty();
    }


    private void verifyMultiResponse(List<LrdCourtVenueResponse> courtVenueResponses) {
        assertThat(courtVenueResponses).hasSize(2);

        courtVenueResponses.forEach(courtVenueResponse -> {
            if (courtVenueResponse.getEpimmsId().equalsIgnoreCase("123")) {
                assertThat(courtVenueResponse.getEpimmsId()).isEqualTo("123");
                assertThat(courtVenueResponse.getClusterId()).isEqualTo("1");
                assertThat(courtVenueResponse.getRegionId()).isEqualTo("regionId");
                assertThat(courtVenueResponse.getRegion()).isEqualTo("region");
                assertThat(courtVenueResponse.getClusterId()).isEqualTo("1");
                assertThat(courtVenueResponse.getClusterName()).isEqualTo("cluster name");
                assertThat(courtVenueResponse.getPostcode()).isEqualTo("postcode");
                assertThat(courtVenueResponse.getVenueName()).isEqualTo("venueName");
                assertThat(courtVenueResponse.getIsCaseManagementLocation()).isEqualTo("Y");
                assertThat(courtVenueResponse.getIsHearingLocation()).isEqualTo("Y");
                assertThat(courtVenueResponse.getServiceCode()).isEqualTo("AAA2");
            } else {
                assertThat(courtVenueResponse.getEpimmsId()).isEqualTo("1234");
                assertThat(courtVenueResponse.getClusterId()).isEqualTo("1");
                assertThat(courtVenueResponse.getRegionId()).isEqualTo("regionId");
                assertThat(courtVenueResponse.getRegion()).isEqualTo("region");
                assertThat(courtVenueResponse.getClusterId()).isEqualTo("1");
                assertThat(courtVenueResponse.getClusterName()).isEqualTo("cluster name");
                assertThat(courtVenueResponse.getPostcode()).isEqualTo("postcode1");
                assertThat(courtVenueResponse.getVenueName()).isEqualTo("venueName1");
                assertThat(courtVenueResponse.getIsCaseManagementLocation()).isEqualTo("N");
                assertThat(courtVenueResponse.getIsHearingLocation()).isEqualTo("N");
                assertThat(courtVenueResponse.getServiceCode()).isEqualTo("AAA3");
            }
        });
    }

    private void verifySingleResponse(LrdCourtVenueResponse lrdCourtVenueResponse) {
        assertThat(lrdCourtVenueResponse.getEpimmsId()).isEqualTo("123");
        assertThat(lrdCourtVenueResponse.getClusterId()).isEqualTo("1");
        assertThat(lrdCourtVenueResponse.getRegionId()).isEqualTo("regionId");
        assertThat(lrdCourtVenueResponse.getRegion()).isEqualTo("region");
        assertThat(lrdCourtVenueResponse.getClusterId()).isEqualTo("1");
        assertThat(lrdCourtVenueResponse.getClusterName()).isEqualTo("cluster name");
        assertThat(lrdCourtVenueResponse.getPostcode()).isEqualTo("postcode");
        assertThat(lrdCourtVenueResponse.getCourtName()).isEqualTo("Court ABC");
        assertThat(lrdCourtVenueResponse.getSiteName()).isEqualTo("COURT ABC");
        assertThat(lrdCourtVenueResponse.getVenueName()).isEqualTo("venueName");
        assertThat(lrdCourtVenueResponse.getIsCaseManagementLocation()).isEqualTo("Y");
        assertThat(lrdCourtVenueResponse.getIsHearingLocation()).isEqualTo("Y");
        assertThat(lrdCourtVenueResponse.getServiceCode()).isEqualTo("AAA2");
    }

    private List<CourtVenue> prepareCourtVenue() {

        var courtVenues = new ArrayList<CourtVenue>();

        courtVenues.add(CourtVenue.builder()
                            .courtVenueId(5L)
                            .epimmsId("123")
                            .region(getRegion())
                            .cluster(getCluster())
                            .postcode("postcode")
                            .clusterId("1")
                            .courtName("courtName")
                            .openForPublic(true)
                            .courtType(getCourtType())
                            .courtName("Court ABC")
                            .siteName("COURT ABC")
                            .venueName("venueName")
                            .isCaseManagementLocation("Y")
                            .isHearingLocation("Y")
                            .welshVenueName("testVenue")
                            .isTemporaryLocation("N")
                            .isNightingaleCourt("N")
                            .locationType("Court")
                            .parentLocation("366559")
                            .welshCourtName("welshCourtName1")
                            .uprn("1234")
                            .venueOuCode("87675")
                            .mrdBuildingLocationId("8686")
                            .mrdVenueId("765")
                            .serviceUrl("https://serviceurl.com")
                            .factUrl("https://facturl.com")
                            .districtRegistryVenueId("DR123")
                            .appealCentreVenueId("AC123")
                            .serviceCode("AAA2")
                            .build());

        return courtVenues;
    }

    private List<CourtVenue> prepareMultiCourtVenueResponse() {
        var courtVenues = new ArrayList<CourtVenue>();
        courtVenues.addAll(prepareCourtVenue());
        courtVenues.add(CourtVenue.builder()
                            .courtVenueId(5L)
                            .epimmsId("1234")
                            .region(getRegion())
                            .cluster(getCluster())
                            .postcode("postcode1")
                            .clusterId("2")
                            .courtName("courtName1")
                            .courtType(getCourtType())
                            .openForPublic(true)
                            .venueName("venueName1")
                            .isCaseManagementLocation("N")
                            .isHearingLocation("N")
                            .welshVenueName("testVenue")
                            .isTemporaryLocation("N")
                            .isNightingaleCourt("N")
                            .locationType("Court")
                            .parentLocation("366559")
                            .welshCourtName("welshCourtName1")
                            .uprn("1234")
                            .venueOuCode("87675")
                            .mrdBuildingLocationId("8686")
                            .mrdVenueId("765")
                            .serviceUrl("https://serviceurl.com")
                            .factUrl("https://facturl.com")
                            .serviceCode("AAA3")
                            .build());

        return courtVenues;
    }


    private Cluster getCluster() {
        Cluster cluster = new Cluster();
        cluster.setClusterId("1");
        cluster.setClusterName("cluster name");
        return cluster;
    }

    private Region getRegion() {
        Region region = new Region();
        region.setRegionId("regionId");
        region.setDescription("region");
        return region;
    }

    private CourtType getCourtType() {
        CourtType courtType = new CourtType();
        courtType.setTypeOfCourt("courtType");
        courtType.setCourtTypeId("courtTypeId");
        return courtType;
    }

    private void stubV2LookupsWithUses(CourtVenueUseProjection... uses) {
        when(courtVenueRepository.findNamesByMrdVenueIdIn(anyList())).thenReturn(List.of());
        when(courtVenueRepository.findAddressesByMrdVenueIdIn(anyList())).thenReturn(List.of());
        when(courtVenueRepository.findContactsByMrdVenueIdIn(anyList())).thenReturn(List.of());
        when(courtVenueRepository.findUsesByMrdVenueIdIn(anyList())).thenReturn(List.of(uses));
        when(courtVenueRepository.findUrlsByMrdVenueIdIn(anyList())).thenReturn(List.of());
        when(courtVenueRepository.findReferenceCodesByMrdVenueIdIn(anyList())).thenReturn(List.of());
    }

    private CourtVenueUseProjection courtVenueUseProjection(String mrdVenueId, String useType) {
        return new CourtVenueUseProjection() {
            @Override
            public String getMrdVenueId() {
                return mrdVenueId;
            }

            @Override
            public String getUseType() {
                return useType;
            }
        };
    }

    private CourtVenueRequestParam buildRequestParamForFilter(String filterName, String filterValue) {
        CourtVenueRequestParam.CourtVenueRequestParamBuilder builder = CourtVenueRequestParam.builder();
        switch (filterName) {
            case "is_hearing_location" -> builder.isHearingLocation(filterValue);
            case "is_case_management_location" -> builder.isCaseManagementLocation(filterValue);
            case "is_temporary_location" -> builder.isTemporaryLocation(filterValue);
            case "is_nightingale_court" -> builder.isNightingaleCourt(filterValue);
            case "is_district_registry" -> builder.isDistrictRegistry(filterValue);
            case "is_appeal_centre" -> builder.isAppealCentre(filterValue);
            default -> throw new IllegalArgumentException("Unsupported filter: " + filterName);
        }
        return builder.build();
    }

}
