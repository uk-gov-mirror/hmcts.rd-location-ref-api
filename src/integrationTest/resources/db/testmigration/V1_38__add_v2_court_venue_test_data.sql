INSERT INTO service (
    service_id, org_unit_id, business_area_id, sub_business_area_id, jurisdiction_id, service_code,
    service_description, service_short_description, last_update
)
VALUES (
    9999, 1, 1, 1, 1, 'SVC9', 'V2 Court Venue Test Service', 'V2 Court Venue Test Service', now()
)
ON CONFLICT (service_code) DO NOTHING;

INSERT INTO court_venue (
    court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id,
    open_for_public, court_address, postcode, court_status, court_name, is_case_management_location,
    is_hearing_location, is_temporary_location, is_nightingale_court, location_type, service_code,
    welsh_court_name, uprn, venue_ou_code, mrd_building_location_id, mrd_venue_id, service_url, fact_url,
    court_status_code, open_date, contact_email, breathing_space_email, is_district_registry, is_appeal_centre
)
VALUES (
    16, '123463', 'Aberdeen Tribunal Hearing Centre 8', '2021-07-05 09:50:46.269032',
    '2021-07-05 09:50:46.269032', '4', '17', '1', true, 'AB8, 54 HUNTLY STREET, ABERDEEN',
    'AB11 1TY', 'Open', 'ABERDEEN TRIBUNAL HEARING CENTRE 24', 'N', 'N', 'N', 'N', 'Court', 'SVC9',
    'Llys Sirol Abertawe', '100070987654', 'VOU987654', 'MRD-BLD-987', 'MRD-987654321',
    'https://service-secondary.example.gov.uk', 'https://fact-secondary.example.gov.uk', 'OPEN', '2021-07-05',
    'secondary.contact@example.gov.uk', 'secondary.breathing.space@example.gov.uk', 'N', 'N'
) ON CONFLICT (court_venue_id) DO NOTHING;

UPDATE court_venue
SET welsh_court_name = 'Llys Sirol Caerdydd',
    uprn = '100070123456',
    venue_ou_code = 'VOU123456',
    mrd_building_location_id = 'MRD-BLD-385',
    mrd_venue_id = 'MRD-123456789',
    service_url = 'https://service.example.gov.uk',
    fact_url = 'https://fact.example.gov.uk',
    court_status_code = 'OPEN',
    open_date = '2021-07-05',
    parent_id = null,
    district_registry_venue_id = null,
    appeal_centre_venue_id = null,
    contact_email = 'contact@example.gov.uk',
    breathing_space_email = 'breathing.space@example.gov.uk',
    is_nightingale_court = 'Y',
    is_district_registry = 'Y',
    is_appeal_centre = 'Y'
WHERE court_venue_id = 15
  AND epimms_id = '123463';

INSERT INTO court_venue_name (mrd_venue_id, court_name_type, language_code, name_desc)
VALUES
    ('MRD-987654321', 'SITE', 'EN', 'Aberdeen Tribunal Hearing Centre 7'),
    ('MRD-987654321', 'COURT', 'EN', 'ABERDEEN TRIBUNAL HEARING CENTRE 22'),
    ('MRD-987654321', 'COURT', 'CY', 'Llys Sirol Abertawe'),
    ('MRD-123456789', 'SITE', 'EN', 'Aberdeen Tribunal Hearing Centre 7'),
    ('MRD-123456789', 'COURT', 'EN', 'ABERDEEN TRIBUNAL HEARING CENTRE 23'),
    ('MRD-123456789', 'COURT', 'CY', 'Llys Sirol Caerdydd')
ON CONFLICT (mrd_venue_id, court_name_type, language_code) DO NOTHING;

INSERT INTO address (mrd_venue_id, address_type, language_code, address, post_code, uprn)
VALUES
    ('MRD-987654321', 'MAILING', 'EN', 'AB7, 54 HUNTLY STREET, ABERDEEN', 'AB11 1TY', '100070987654'),
    ('MRD-123456789', 'MAILING', 'EN', 'AB7, 54 HUNTLY STREET, ABERDEEN', 'AB11 1TY', '100070123456')
ON CONFLICT (mrd_venue_id, address_type, language_code) DO NOTHING;

INSERT INTO contact_details (mrd_venue_id, contact_method_code, contact_type_code, contact_value)
VALUES
    ('MRD-987654321', 'EMAIL', 'CONTACT_SERVICE', 'secondary.contact@example.gov.uk'),
    ('MRD-987654321', 'EMAIL', 'BREATHING_SPACE', 'secondary.breathing.space@example.gov.uk'),
    ('MRD-123456789', 'EMAIL', 'CONTACT_SERVICE', 'contact@example.gov.uk'),
    ('MRD-123456789', 'EMAIL', 'BREATHING_SPACE', 'breathing.space@example.gov.uk')
ON CONFLICT (mrd_venue_id, contact_method_code, contact_type_code) DO NOTHING;

INSERT INTO court_use_mapping (mrd_venue_id, use_type_code)
VALUES
    ('MRD-123456789', 'CASE_MANAGEMENT'),
    ('MRD-123456789', 'HEARING'),
    ('MRD-123456789', 'TEMPORARY'),
    ('MRD-123456789', 'NIGHTINGALE'),
    ('MRD-123456789', 'DISTRICT_REGISTRY'),
    ('MRD-123456789', 'APPEAL_CENTRE')
ON CONFLICT (mrd_venue_id, use_type_code) DO NOTHING;

INSERT INTO reference_codes (mrd_venue_id, reference_code_type, reference_code)
VALUES
    ('MRD-987654321', 'VENUE_OU_CODE', 'VOU987654'),
    ('MRD-123456789', 'VENUE_OU_CODE', 'VOU123456')
ON CONFLICT (mrd_venue_id, reference_code_type, reference_code) DO NOTHING;

INSERT INTO court_venue_url (mrd_venue_id, url_type, url)
VALUES
    ('MRD-987654321', 'SERVICE', 'https://service-secondary.example.gov.uk'),
    ('MRD-987654321', 'FACT', 'https://fact-secondary.example.gov.uk'),
    ('MRD-123456789', 'SERVICE', 'https://service.example.gov.uk'),
    ('MRD-123456789', 'FACT', 'https://fact.example.gov.uk')
ON CONFLICT (mrd_venue_id, url_type) DO NOTHING;
