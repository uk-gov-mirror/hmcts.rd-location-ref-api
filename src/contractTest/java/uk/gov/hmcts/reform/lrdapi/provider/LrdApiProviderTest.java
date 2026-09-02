package uk.gov.hmcts.reform.lrdapi.provider;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerConsumerVersionSelectors;
import au.com.dius.pact.provider.junitsupport.loader.SelectorBuilder;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.lrdapi.controllers.LrdApiController;
import uk.gov.hmcts.reform.lrdapi.controllers.LrdCourtVenueController;
import uk.gov.hmcts.reform.lrdapi.domain.BuildingLocation;
import uk.gov.hmcts.reform.lrdapi.domain.Cluster;
import uk.gov.hmcts.reform.lrdapi.domain.CourtType;
import uk.gov.hmcts.reform.lrdapi.domain.CourtTypeServiceAssoc;
import uk.gov.hmcts.reform.lrdapi.domain.CourtVenue;
import uk.gov.hmcts.reform.lrdapi.domain.Jurisdiction;
import uk.gov.hmcts.reform.lrdapi.domain.OrgBusinessArea;
import uk.gov.hmcts.reform.lrdapi.domain.OrgSubBusinessArea;
import uk.gov.hmcts.reform.lrdapi.domain.OrgUnit;
import uk.gov.hmcts.reform.lrdapi.domain.Region;
import uk.gov.hmcts.reform.lrdapi.domain.Service;
import uk.gov.hmcts.reform.lrdapi.domain.ServiceToCcdCaseTypeAssoc;
import uk.gov.hmcts.reform.lrdapi.repository.BuildingLocationRepository;
import uk.gov.hmcts.reform.lrdapi.repository.CourtTypeServiceAssocRepository;
import uk.gov.hmcts.reform.lrdapi.repository.CourtVenueRepository;
import uk.gov.hmcts.reform.lrdapi.repository.RegionRepository;
import uk.gov.hmcts.reform.lrdapi.repository.ServiceRepository;
import uk.gov.hmcts.reform.lrdapi.repository.ServiceToCcdCaseTypeAssocRepositry;
import uk.gov.hmcts.reform.lrdapi.service.impl.CourtVenueServiceImpl;
import uk.gov.hmcts.reform.lrdapi.service.impl.LrdBuildingLocationServiceImpl;
import uk.gov.hmcts.reform.lrdapi.service.impl.LrdServiceImpl;
import uk.gov.hmcts.reform.lrdapi.service.impl.RegionServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Provider("referenceData_location")
@PactBroker(url = "${PACT_BROKER_FULL_URL:http://localhost:9292}",
    providerTags = "${pactbroker.providerTags:master}",
    enablePendingPacts = "${pactbroker.enablePending:true}")
@ContextConfiguration(classes = {LrdApiController.class, LrdCourtVenueController.class, LrdServiceImpl.class,
    LrdBuildingLocationServiceImpl.class, RegionServiceImpl.class, CourtVenueServiceImpl.class})
@TestPropertySource(properties = {"loggingComponentName=LrdApiProviderTest"})
@IgnoreNoPactsToVerify
public class LrdApiProviderTest {

    @PactBrokerConsumerVersionSelectors
    public static SelectorBuilder consumerVersionSelectors() {
        String branch = System.getProperty("pactbroker.consumerBranch", "");
        if (!branch.isBlank()) {
            return new SelectorBuilder().branch(branch);
        }
        return new SelectorBuilder().tag(System.getProperty("pactbroker.consumerTag", "master"));
    }

    public static final String CLUSTER_NAME = "ClusterXYZ";
    public static final String REGION = "Region XYZ";
    public static final String VENUE_NAME = "venueName";
    public static final String WELSH_COURT_NAME_1 = "welshCourtName1";
    public static final String VENUE_CODE = "87675";
    public static final String SERVICE_URL = "https://serviceurl.com";
    public static final String FACT_URL = "https://facturl.com";
    public static final String COURT_FINDER_URL = "https://testUrl.com";
    public static final String COURT = "Court";
    @MockitoBean
    ServiceRepository serviceRepository;

    @MockitoBean
    RegionRepository regionRepository;

    @MockitoBean
    ServiceToCcdCaseTypeAssocRepositry serviceToCcdCaseTypeAssocRepositry;

    @MockitoBean
    BuildingLocationRepository buildingLocationRepository;

    @MockitoBean
    CourtTypeServiceAssocRepository courtTypeServiceAssocRepository;

    @MockitoBean
    CourtVenueRepository courtVenueRepository;

    @Autowired
    LrdApiController lrdApiController;

    @Autowired
    LrdCourtVenueController lrdCourtVenueController;


    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        if (context != null) {
            context.verifyInteraction();
        }
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        MockMvcTestTarget testTarget = new MockMvcTestTarget();
        testTarget.setControllers(
            lrdApiController, lrdCourtVenueController);
        if (nonNull(context)) {
            context.setTarget(testTarget);
        }

    }

    @State({"Organisational Service details exist"})
    public void toReturnOrganisationalServiceDetails() {
        OrgUnit orgUnit = new OrgUnit(1L, "orgUnit");
        Service service = new Service();
        service.setServiceId(1L);
        service.setOrgUnit(orgUnit);
        service.setServiceCode("AAA1");
        service.setServiceDescription("Insolvency");
        service.setServiceShortDescription("Insolvency");
        OrgSubBusinessArea orgSubBusArea =
            new OrgSubBusinessArea(1L, "OrgSubBusinessArea");
        OrgBusinessArea orgBusinessArea = new OrgBusinessArea(1L, "BusinessArea");
        Jurisdiction jurisdiction = new Jurisdiction(1L, "Jurisdiction");
        service.setOrgBusinessArea(orgBusinessArea);
        service.setOrgSubBusinessArea(orgSubBusArea);
        service.setJurisdiction(jurisdiction);
        service.setLastUpdate(LocalDateTime.now());
        ServiceToCcdCaseTypeAssoc serviceToCcdCaseTypeAssoc = new ServiceToCcdCaseTypeAssoc();
        serviceToCcdCaseTypeAssoc.setId(1L);
        serviceToCcdCaseTypeAssoc.setCcdCaseType("CCDCASETYPE1");
        serviceToCcdCaseTypeAssoc.setCcdServiceName("CCDSERVICENAME");
        serviceToCcdCaseTypeAssoc.setService(service);
        service.setServiceToCcdCaseTypeAssocs(Collections.singletonList(serviceToCcdCaseTypeAssoc));
        List<Service> services = new ArrayList<>();
        services.add(service);
        when(serviceRepository.findByServiceCode(any())).thenReturn(service);
        when(serviceRepository.findAll()).thenReturn(services);
        when(serviceToCcdCaseTypeAssocRepositry.findByCcdCaseTypeIgnoreCase(any()))
            .thenReturn(List.of(serviceToCcdCaseTypeAssoc));
        when(serviceToCcdCaseTypeAssocRepositry.findByCcdServiceNameInIgnoreCase(any()))
            .thenReturn(List.of(serviceToCcdCaseTypeAssoc));
    }

    @State({"Building Location details exist for the request provided"})
    public void toReturnBuildingLocationDetails() {
        Cluster cluster = new Cluster();
        cluster.setClusterId("456");
        cluster.setClusterName(CLUSTER_NAME);
        cluster.setWelshClusterName("ClusterABC");
        cluster.setCreatedTime(LocalDateTime.now());
        cluster.setUpdatedTime(LocalDateTime.now());

        Region region = new Region();
        region.setCreatedTime(LocalDateTime.now());
        region.setDescription(REGION);
        region.setRegionId("123");
        region.setUpdatedTime(LocalDateTime.now());
        region.setWelshDescription("Region ABC");

        CourtType courtType = getCourtType();

        CourtVenue courtVenue = CourtVenue.builder()
            .courtTypeId("17")
            .courtType(courtType)
            .siteName("Aberdeen Tribunal Hearing Centre 1")
            .openForPublic(Boolean.TRUE)
            .epimmsId("123456789")
            .region(region)
            .courtName("ABERDEEN TRIBUNAL HEARING CENTRE 1")
            .courtStatus("Open")
            .postcode("AB11 6LT")
            .courtAddress("AB1, 48 HUNTLY STREET, ABERDEEN")
            .courtVenueId(1L)
            .venueName(VENUE_NAME)
            .isCaseManagementLocation("Y")
            .isHearingLocation("Y")
            .welshCourtName(WELSH_COURT_NAME_1)
            .uprn("1234")
            .venueOuCode(VENUE_CODE)
            .mrdBuildingLocationId("8686")
            .mrdVenueId("765")
            .serviceUrl(SERVICE_URL)
            .factUrl(FACT_URL)
            .build();

        Set<CourtVenue> courtVenues = new HashSet<>();
        courtVenues.add(courtVenue);

        BuildingLocation buildingLocation = BuildingLocation.builder()
            .buildingLocationStatus("OPEN")
            .area("Area 1")
            .buildingLocationId(123L)
            .buildingLocationName("Taylor House Tribunal Hearing Centre")
            .courtFinderUrl(COURT_FINDER_URL)
            .created(LocalDateTime.now())
            .epimmsId("4567")
            .lastUpdated(LocalDateTime.now())
            .postcode("XY2 YY3")
            .region(region)
            .cluster(cluster)
            .address("Address 123")
            .courtVenues(courtVenues)
            .build();

        BuildingLocation buildingLocation2 = BuildingLocation.builder()
            .buildingLocationStatus("CLOSED")
            .area("Area 2")
            .buildingLocationId(1234L)
            .buildingLocationName("Taylor House Tribunal Hearing Centre 2")
            .courtFinderUrl(COURT_FINDER_URL)
            .created(LocalDateTime.now())
            .epimmsId("45678")
            .lastUpdated(LocalDateTime.now())
            .postcode("XY21 YY3")
            .region(region)
            .cluster(cluster)
            .address("Address 123456")
            .courtVenues(courtVenues)
            .build();

        when(buildingLocationRepository.findByBuildingLocationNameIgnoreCase(anyString())).thenReturn(buildingLocation);
        when(buildingLocationRepository.findByEpimmsId(anyList())).thenReturn(List.of(buildingLocation));
        when(buildingLocationRepository.findAll()).thenReturn(List.of(buildingLocation, buildingLocation2));
        when(buildingLocationRepository.findByBuildingLocationStatusOpen()).thenReturn(List.of(buildingLocation));
        when(buildingLocationRepository.findByClusterId(anyString()))
            .thenReturn(List.of(buildingLocation, buildingLocation2));
        when(buildingLocationRepository.findByRegionId(anyString()))
            .thenReturn(List.of(buildingLocation, buildingLocation2));
    }

    @State({"Region Details exist"})
    public void toReturnRegionDetails() {
        Region region = new Region("1", "National", "");
        Region region1 = new Region("2", "London", "");
        List<Region> regions = asList(region, region1);
        when(regionRepository.findByDescriptionInIgnoreCase(any())).thenReturn(regions);
        when(regionRepository.findByRegionIdIn(any())).thenReturn(regions);
        when(regionRepository.findByApiEnabled(any())).thenReturn(regions);
    }


    @State({"Court Venues exist for the service code provided"})
    public void toReturnCourtVenuesByServiceCode() {
        Cluster cluster = getCluster();

        Region region = getRegion();

        CourtType courtType = getCourtType();

        CourtVenue courtVenue = getCourtVenue(cluster, region, courtType);

        courtType.setCourtVenues(Collections.singletonList(courtVenue));

        CourtTypeServiceAssoc courtTypeServiceAssoc = new CourtTypeServiceAssoc();
        courtTypeServiceAssoc.setCourtType(courtType);
        when(courtTypeServiceAssocRepository.findByServiceCode(anyString())).thenReturn(courtTypeServiceAssoc);
        when(courtVenueRepository.findByServiceCode(anyString())).thenReturn(List.of(courtVenue));
    }

    @State({"Court Venues exist for the input request provided"})
    public void toReturnCourtVenues() {
        Cluster cluster = getCluster();

        Region region = getRegion();

        CourtType courtType = getCourtType();

        var courtVenues = getMultipleCourtVenues(cluster, region, courtType);

        courtType.setCourtVenues(courtVenues);

        when(courtVenueRepository.findByEpimmsIdIn(anyList())).thenReturn(courtVenues);
        when(courtVenueRepository.findAllWithOpenCourtStatus()).thenReturn(courtVenues);
        when(courtVenueRepository.findByClusterIdWithOpenCourtStatus(anyString())).thenReturn(courtVenues);
        when(courtVenueRepository.findByCourtTypeIdWithOpenCourtStatus(anyString())).thenReturn(courtVenues);
        when(courtVenueRepository.findByRegionIdWithOpenCourtStatus(anyString())).thenReturn(courtVenues);
        when(courtVenueRepository.findAll()).thenReturn(courtVenues);
        when(courtVenueRepository.findByCourtVenueNameOrSiteName(anyString())).thenReturn(courtVenues);
        when(courtVenueRepository.findByCourtTypeIdServiceCodeAndEpimmsIdWithOpenCourtStatus(anyList(),
                                                                                             anyString(), any()))
            .thenReturn(courtVenues);
    }

    @State({"Court Venues exist for the search string provided"})
    public void toReturnCourtVenuesBySearchString() {
        Cluster cluster = getCluster();

        Region region = getRegion();

        CourtType courtType = getCourtType();

        var courtVenues = getMultipleCourtVenues(cluster, region, courtType);

        courtType.setCourtVenues(courtVenues);

        when(courtVenueRepository.findBySearchStringAndCourtTypeId(
            any(),any(),any(),any(),any(),any(),any())).thenReturn(courtVenues);
    }

    private CourtVenue getCourtVenue(Cluster cluster, Region region, CourtType courtType) {
        return CourtVenue.builder()
            .courtVenueId(1L)
            .epimmsId("12345")
            .siteName("siteName1")
            .region(region)
            .courtType(courtType)
            .cluster(cluster)
            .openForPublic(Boolean.TRUE)
            .courtAddress("courtAddress1")
            .postcode("AB EYP")
            .phoneNumber("12232423")
            .closedDate(LocalDateTime.now())
            .courtLocationCode("courtLocationCode1")
            .dxAddress("dxAddress1")
            .courtStatus("Open")
            .courtName("courtName1")
            .venueName(VENUE_NAME)
            .isCaseManagementLocation("Y")
            .isHearingLocation("Y")
            .welshVenueName("testVenue")
            .isTemporaryLocation("N")
            .isNightingaleCourt("N")
            .locationType(COURT)
            .parentLocation("366559")
            .welshCourtName(WELSH_COURT_NAME_1)
            .uprn("1234")
            .venueOuCode(VENUE_CODE)
            .mrdBuildingLocationId("8686")
            .mrdVenueId("765")
            .serviceUrl(SERVICE_URL)
            .factUrl(FACT_URL)
            .build();
    }

    private List<CourtVenue> getMultipleCourtVenues(Cluster cluster, Region region, CourtType courtType) {
        CourtVenue firstCourtVenue = CourtVenue.builder()
            .courtVenueId(1L)
            .epimmsId("12345")
            .siteName("siteName")
            .region(region)
            .courtType(courtType)
            .cluster(cluster)
            .openForPublic(Boolean.TRUE)
            .courtAddress("courtAddress")
            .postcode("AB EYZ")
            .phoneNumber("122324234")
            .closedDate(LocalDateTime.now())
            .courtLocationCode("courtLocationCode")
            .dxAddress("dxAddress")
            .courtStatus("Open")
            .courtName("courtName")
            .venueName(VENUE_NAME)
            .isCaseManagementLocation("Y")
            .isHearingLocation("Y")
            .welshVenueName("testVenue1")
            .isTemporaryLocation("N")
            .isNightingaleCourt("N")
            .locationType(COURT)
            .parentLocation("366559")
            .welshCourtName(WELSH_COURT_NAME_1)
            .uprn("1234")
            .venueOuCode(VENUE_CODE)
            .mrdBuildingLocationId("8686")
            .mrdVenueId("765")
            .serviceUrl(SERVICE_URL)
            .factUrl(FACT_URL)
            .build();

        CourtVenue secondCourtVenue = CourtVenue.builder()
            .courtVenueId(1L)
            .epimmsId("123456")
            .siteName("siteName")
            .region(region)
            .courtType(courtType)
            .cluster(cluster)
            .openForPublic(Boolean.TRUE)
            .courtAddress("courtAddress")
            .postcode("AB EYZ")
            .phoneNumber("122324234")
            .closedDate(LocalDateTime.now())
            .courtLocationCode("courtLocationCode")
            .dxAddress("dxAddress")
            .courtStatus("Closed")
            .courtName("courtName")
            .venueName(VENUE_NAME)
            .isCaseManagementLocation("Y")
            .isHearingLocation("Y")
            .welshVenueName("testVenue2")
            .isTemporaryLocation("N")
            .isNightingaleCourt("N")
            .locationType(COURT)
            .parentLocation("372653")
            .welshCourtName(WELSH_COURT_NAME_1)
            .uprn("1234")
            .venueOuCode(VENUE_CODE)
            .mrdBuildingLocationId("8686")
            .mrdVenueId("765")
            .serviceUrl(SERVICE_URL)
            .factUrl(FACT_URL)
            .build();

        return List.of(firstCourtVenue, secondCourtVenue);
    }


    @State({"Building Location details exist for the searchString provided"})
    public void toReturnBuildingLocationDetailsForSearchString() {
        Cluster cluster = new Cluster();
        cluster.setClusterId("456");
        cluster.setClusterName(CLUSTER_NAME);
        cluster.setWelshClusterName("ClusterABC");
        cluster.setCreatedTime(LocalDateTime.now());
        cluster.setUpdatedTime(LocalDateTime.now());

        Region region = new Region();
        region.setCreatedTime(LocalDateTime.now());
        region.setDescription(REGION);
        region.setRegionId("123");
        region.setUpdatedTime(LocalDateTime.now());
        region.setWelshDescription("Region ABC");

        BuildingLocation buildingLocation = BuildingLocation.builder()
            .buildingLocationStatus("OPEN")
            .area("Area 1")
            .buildingLocationId(123L)
            .buildingLocationName("Taylor House Tribunal Hearing Centre")
            .courtFinderUrl(COURT_FINDER_URL)
            .created(LocalDateTime.now())
            .epimmsId("4567")
            .lastUpdated(LocalDateTime.now())
            .postcode("XY2 YY3")
            .region(region)
            .cluster(cluster)
            .address("Address 123")
            .build();

        BuildingLocation buildingLocation2 = BuildingLocation.builder()
            .buildingLocationStatus("CLOSED")
            .area("Area 2")
            .buildingLocationId(1234L)
            .buildingLocationName("Taylor House Tribunal Hearing Centre 2")
            .courtFinderUrl(COURT_FINDER_URL)
            .created(LocalDateTime.now())
            .epimmsId("45678")
            .lastUpdated(LocalDateTime.now())
            .postcode("XY21 YY3")
            .region(region)
            .cluster(cluster)
            .address("Address 123456")
            .build();

        when(buildingLocationRepository.findByBuildingLocationNamesBySearch(anyString()))
            .thenReturn(List.of(buildingLocation, buildingLocation2));

    }

    private CourtType getCourtType() {
        CourtType courtType = new CourtType();
        courtType.setCourtTypeId("17");
        courtType.setTypeOfCourt("Immigration and Asylum");
        return courtType;
    }


    private Region getRegion() {
        Region region = new Region();
        region.setDescription(REGION);
        region.setRegionId("123");
        return region;
    }

    private Cluster getCluster() {
        Cluster cluster = new Cluster();
        cluster.setClusterId("456");
        cluster.setClusterName(CLUSTER_NAME);
        return cluster;
    }

}
