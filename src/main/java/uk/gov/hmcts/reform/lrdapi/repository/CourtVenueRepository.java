package uk.gov.hmcts.reform.lrdapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.hmcts.reform.lrdapi.domain.CourtVenue;
import uk.gov.hmcts.reform.lrdapi.repository.projection.CourtVenueAddressProjection;
import uk.gov.hmcts.reform.lrdapi.repository.projection.CourtVenueContactProjection;
import uk.gov.hmcts.reform.lrdapi.repository.projection.CourtVenueNameProjection;
import uk.gov.hmcts.reform.lrdapi.repository.projection.CourtVenueReferenceCodeProjection;
import uk.gov.hmcts.reform.lrdapi.repository.projection.CourtVenueUrlProjection;
import uk.gov.hmcts.reform.lrdapi.repository.projection.CourtVenueUseProjection;

import java.util.List;

public interface CourtVenueRepository extends JpaRepository<CourtVenue, Long> {

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where cv.epimmsId in (:epimmsIdList)")
    List<CourtVenue> findByEpimmsIdIn(List<String> epimmsIdList);

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where cv.epimmsId in (:epimmsIdList) and cv.courtStatus='Open'")
    List<CourtVenue> findByEpimmsIdInWithOpenCourtStatus(List<String> epimmsIdList);

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where upper(cv.mrdVenueId) = upper(:mrdVenueId)")
    List<CourtVenue> findByMrdVenueId(String mrdVenueId);

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where upper(cv.mrdVenueId) = upper(:mrdVenueId) and cv.courtStatus='Open'")
    List<CourtVenue> findByMrdVenueIdWithOpenCourtStatus(String mrdVenueId);

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where upper(cv.mrdVenueId) in (:mrdVenueIds)")
    List<CourtVenue> findByMrdVenueIdIn(List<String> mrdVenueIds);

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where upper(cv.mrdVenueId) in (:mrdVenueIds) and cv.courtStatus='Open'")
    List<CourtVenue> findByMrdVenueIdInWithOpenCourtStatus(List<String> mrdVenueIds);

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where cv.courtTypeId = :courtTypeId and cv.courtStatus='Open'")
    List<CourtVenue> findByCourtTypeIdWithOpenCourtStatus(String courtTypeId);

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where cv.courtTypeId = :courtTypeId")
    List<CourtVenue> findByCourtTypeId(String courtTypeId);

    @Query(value = """
             select cv from court_venue cv LEFT JOIN FETCH cv.courtType
             LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region
             where cv.courtStatus='Open'
             and (:serviceCode is null or upper(cv.serviceCode) = :serviceCode)
             and (:courtTypeId is null or cv.courtTypeId = :courtTypeId)
             and (:epimmsIdList is null or cv.epimmsId in (:epimmsIdList))
             and (:courtTypeId is not null or :epimmsIdList is not null)
             """)
    List<CourtVenue> findByCourtTypeIdServiceCodeAndEpimmsIdWithOpenCourtStatus(List<String> epimmsIdList,
                                                                                String courtTypeId,
                                                                     String serviceCode);

    @Query(value = """
             select cv from court_venue cv LEFT JOIN FETCH cv.courtType
             LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region
             where (:serviceCode is null or upper(cv.serviceCode) = :serviceCode)
             and (:courtTypeId is null or cv.courtTypeId = :courtTypeId)
             and (:epimmsIdList is null or cv.epimmsId in (:epimmsIdList))
             and (:courtTypeId is not null or :epimmsIdList is not null)
             """)
    List<CourtVenue> findByCourtTypeIdServiceCodeAndEpimmsId(List<String> epimmsIdList,
                                                             String courtTypeId,
                                                             String serviceCode);


    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where cv.regionId = :regionId and cv.courtStatus='Open'")
    List<CourtVenue> findByRegionIdWithOpenCourtStatus(String regionId);

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where cv.regionId = :regionId")
    List<CourtVenue> findByRegionId(String regionId);


    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where cv.clusterId = :clusterId and cv.courtStatus='Open'")
    List<CourtVenue> findByClusterIdWithOpenCourtStatus(String clusterId);

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where cv.clusterId = :clusterId")
    List<CourtVenue> findByClusterId(String clusterId);

    @Query(value = "select cv from court_venue cv "
        + "where upper(cv.courtName) = upper(:courtVenueName) "
        + "or upper(cv.siteName) = upper(:courtVenueName)")
    List<CourtVenue> findByCourtVenueNameOrSiteName(String courtVenueName);

    @Query(value = "select cv from court_venue cv "
        + "where (upper(cv.courtName) = upper(:courtVenueName) "
        + "or upper(cv.siteName) = upper(:courtVenueName)) "
        + "and cv.courtStatus='Open'")
    List<CourtVenue> findByCourtVenueNameOrSiteNameWithOpenCourtStatus(String courtVenueName);

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where cv.courtStatus='Open'")
    List<CourtVenue> findAllWithOpenCourtStatus();

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region ")
    List<CourtVenue> findAllCourtVenues();

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where cv.courtStatus='Open' "
        + "and (coalesce(:courtTypeId) is null or (cv.courtTypeId in (:courtTypeId))) "
        + "and (coalesce(:serviceCode) is null or (cv.serviceCode in (:serviceCode))) "
        + "and ((:isCaseManagementLocation) is null or (cv.isCaseManagementLocation in (:isCaseManagementLocation))) "
        + "and ((:isHearingLocation) is null or (cv.isHearingLocation in (:isHearingLocation))) "
        + "and ((:locationType) is null or (cv.locationType in (:locationType))) "
        + "and ((:isTemporaryLocation) is null or (cv.isTemporaryLocation in (:isTemporaryLocation))) "
        + "and (upper(cv.siteName) like %:searchString% "
        + "or upper(cv.courtName) like %:searchString% "
        + "or upper(cv.postcode) like %:searchString% "
        + "or upper(cv.courtAddress) like %:searchString%)")
    List<CourtVenue> findBySearchStringAndCourtTypeId(String searchString, List<String> courtTypeId,
                                                      List<String> serviceCode,String isCaseManagementLocation,
                                                      String isHearingLocation,String locationType,
                                                      String isTemporaryLocation);


    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where upper(cv.serviceCode) = upper(:serviceCode)")
    List<CourtVenue> findByServiceCode(String serviceCode);

    @Query(value = "select cv from court_venue cv LEFT JOIN FETCH cv.courtType"
        + " LEFT JOIN FETCH cv.cluster LEFT JOIN FETCH cv.region "
        + "where upper(cv.serviceCode) = upper(:serviceCode) "
        + "and cv.courtStatus='Open'")
    List<CourtVenue> findByServiceCodeWithOpenCourtStatus(String serviceCode);

    @Query(value = """
        select
            mrd_venue_id as mrdVenueId,
            court_name_type as type,
            language_code as language,
            name_desc as name
        from court_venue_name
        where mrd_venue_id in (:mrdVenueIds)
        order by mrd_venue_id, court_name_type, language_code
        """, nativeQuery = true)
    List<CourtVenueNameProjection> findNamesByMrdVenueIdIn(List<String> mrdVenueIds);

    @Query(value = """
        select
            mrd_venue_id as mrdVenueId,
            address_type as type,
            address,
            post_code as postCode,
            uprn,
            language_code as language
        from address
        where mrd_venue_id in (:mrdVenueIds)
        order by mrd_venue_id, address_type, language_code
        """, nativeQuery = true)
    List<CourtVenueAddressProjection> findAddressesByMrdVenueIdIn(List<String> mrdVenueIds);

    @Query(value = """
        select
            mrd_venue_id as mrdVenueId,
            contact_method_code as method,
            contact_type_code as type,
            contact_value as value
        from contact_details
        where mrd_venue_id in (:mrdVenueIds)
        order by mrd_venue_id, contact_method_code, contact_type_code
        """, nativeQuery = true)
    List<CourtVenueContactProjection> findContactsByMrdVenueIdIn(List<String> mrdVenueIds);

    @Query(value = """
        select
            mrd_venue_id as mrdVenueId,
            use_type_code as useType
        from court_use_mapping
        where mrd_venue_id in (:mrdVenueIds)
        order by mrd_venue_id, use_type_code
        """, nativeQuery = true)
    List<CourtVenueUseProjection> findUsesByMrdVenueIdIn(List<String> mrdVenueIds);

    @Query(value = """
        select
            mrd_venue_id as mrdVenueId,
            url_type as type,
            url
        from court_venue_url
        where mrd_venue_id in (:mrdVenueIds)
        order by mrd_venue_id, url_type
        """, nativeQuery = true)
    List<CourtVenueUrlProjection> findUrlsByMrdVenueIdIn(List<String> mrdVenueIds);

    @Query(value = """
        select
            mrd_venue_id as mrdVenueId,
            reference_code_type as type,
            reference_code as value
        from reference_codes
        where mrd_venue_id in (:mrdVenueIds)
        order by mrd_venue_id, reference_code_type, reference_code
        """, nativeQuery = true)
    List<CourtVenueReferenceCodeProjection> findReferenceCodesByMrdVenueIdIn(List<String> mrdVenueIds);
}
