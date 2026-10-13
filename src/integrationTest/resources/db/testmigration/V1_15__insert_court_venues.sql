insert into
  building_location(
    building_location_id,
    region_id,
    building_location_status,
    cluster_id,
    epimms_id,
    building_location_name,
    area,
    court_finder_url,
    postcode,
    address,
    updated_time)
values(
  '22042006',
  '2',
  'Closed',
  '01234',
  '123EpimmsId456',
  'Building Location 4',
  'Area D',
  'Court Finder URL 4',
  'EC2A 3AQ',
  '4 Street, London',
  now());

insert into
  building_location(
    building_location_id,
    region_id,
    building_location_status,
    cluster_id,
    epimms_id,
    building_location_name,
    area,
    court_finder_url,
    postcode,
    address,
    updated_time)
values(
  '22042008',
  '2',
  'Open',
  '01234',
  '123457',
  'Building Location 4',
  'Area D',
  'Court Finder URL 4',
  'EC2A 3AQ',
  '4 Street, London',
  now());

insert into court_venue (court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id, open_for_public, court_address, postcode, phone_number, closed_date, court_location_code, dx_address, welsh_site_name, welsh_court_address, court_status, court_open_date, court_name, venue_name, is_case_management_location, is_hearing_location, welsh_venue_name, is_temporary_location, is_nightingale_court, location_type, parent_location)
values(1, '123456787','Aberdeen Tribunal Hearing Centre 1',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','1','23', null,true,'AB1, 48 HUNTLY STREET, ABERDEEN','AB11 6LT',null,null,null,null,null,null,'Open', null,'ABERDEEN TRIBUNAL HEARING CENTRE 1','venueName1','Y','Y','testVenue','N','N','Court','366559');
insert into court_venue (court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id, open_for_public, court_address, postcode, phone_number, closed_date, court_location_code, dx_address, welsh_site_name, welsh_court_address, court_status, court_open_date, court_name, venue_name, is_case_management_location, is_hearing_location)
values(2, '123456788','Aberdeen Tribunal Hearing Centre 2',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','1','17', '1',true,'AB2, 49 HUNTLY STREET, ABERDEEN','AB11 7KT',null,null,null,null,null,null,'Open', null,'ABERDEEN TRIBUNAL HEARING CENTRE 2',null,null,null);
insert into court_venue (court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id, open_for_public, court_address, postcode, phone_number, closed_date, court_location_code, dx_address, welsh_site_name, welsh_court_address, court_status, court_open_date, court_name, venue_name, is_case_management_location, is_hearing_location)
values(3, '123456789','Aberdeen Tribunal Hearing Centre 3',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','2','17', '1',true,'AB3, 50 HUNTLY STREET, ABERDEEN','AB11 8IP',null,null,null,null,null,null,'Open', null,'ABERDEEN TRIBUNAL HEARING CENTRE 3',null,null,null);
insert into court_venue (court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id, open_for_public, court_address, postcode, phone_number, closed_date, court_location_code, dx_address, welsh_site_name, welsh_court_address, court_status, court_open_date, court_name, venue_name, is_case_management_location, is_hearing_location)
values(4, 'epimmsId1234','Aberdeen Tribunal Hearing Centre 4',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','1','17', null,true,'AB4, 51 HUNTLY STREET, ABERDEEN','AB11 4RT',null,null,null,null,null,null,'Open', null,'ABERDEEN TRIBUNAL HEARING CENTRE 4',null,null,null);
insert into court_venue (court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id, open_for_public, court_address, postcode, phone_number, closed_date, court_location_code, dx_address, welsh_site_name, welsh_court_address, court_status, court_open_date, court_name, venue_name, is_case_management_location, is_hearing_location)
values(5, 'epimmsId1235','Aberdeen Tribunal Hearing Centre 5',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','1','17', '1',true,'AB5, 52 HUNTLY STREET, ABERDEEN','AB11 4EQ',null,null,null,null,null,null,'Open', null,'ABERDEEN TRIBUNAL HEARING CENTRE 5',null,null,null);
insert into court_venue (court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id, open_for_public, court_address, postcode, phone_number, closed_date, court_location_code, dx_address, welsh_site_name, welsh_court_address, court_status, court_open_date, court_name, venue_name, is_case_management_location, is_hearing_location)
values(6, 'epimmsId1236','Aberdeen Tribunal Hearing Centre 6',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','2','17', null,true,'AB6, 53 HUNTLY STREET, ABERDEEN','AB11 7GQ',null,null,null,null,null,null,'Open', null,'ABERDEEN TRIBUNAL HEARING CENTRE 6',null,null,null);
insert into court_venue (court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id, open_for_public, court_address, postcode, phone_number, closed_date, court_location_code, dx_address, welsh_site_name, welsh_court_address, court_status, court_open_date, court_name, venue_name, is_case_management_location, is_hearing_location)
values(7, 'epimmsId1237','Aberdeen Tribunal Hearing Centre 7',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','2','17', '2',true,'AB7, 54 HUNTLY STREET, ABERDEEN','AB11 1TY',null,null,null,null,null,null,'Open', null,'ABERDEEN TRIBUNAL HEARING CENTRE 7',null,null,null);
insert into court_venue (court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id, open_for_public, court_address, postcode, phone_number, closed_date, court_location_code, dx_address, welsh_site_name, welsh_court_address, court_status, court_open_date, court_name, venue_name, is_case_management_location, is_hearing_location)
values(8, '123456','Aberdeen Tribunal Hearing Centre 8',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','2','17', '2',true,'AB8, 55 HUNTLY STREET, ABERDEEN','AB11 2HT',null,null,null,null,null,null,'Open', null,'ABERDEEN TRIBUNAL HEARING CENTRE 8',null,null,null);
insert into court_venue (court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id, open_for_public, court_address, postcode, phone_number, closed_date, court_location_code, dx_address, welsh_site_name, welsh_court_address, court_status, court_open_date, court_name, venue_name, is_case_management_location, is_hearing_location)
values(9, '123457','Aberdeen Tribunal Hearing Centre 9',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','1','17', null,true,'AB9, 56 HUNTLY STREET, ABERDEEN','AB11 3RP',null,null,null,null,null,null,'Open', null,'ABERDEEN TRIBUNAL HEARING CENTRE 9',null,null,null);
insert into court_venue (court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id, open_for_public, court_address, postcode, phone_number, closed_date, court_location_code, dx_address, welsh_site_name, welsh_court_address, court_status, court_open_date, court_name, venue_name, is_case_management_location, is_hearing_location,location_type,is_temporary_location)
values(10, '123458','Aberdeen Tribunal Hearing Centre 10',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','1','17', '2',true,'AB10, 57 HUNTLY STREET, ABERDEEN','AB11 5QA',null,null,null,null,null,null,'Open', null,'ABERDEEN TRIBUNAL HEARING CENTRE 10',null,'Y','Y','CTSC','Y');
insert into court_venue (court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id, open_for_public, court_address, postcode, phone_number, closed_date, court_location_code, dx_address, welsh_site_name, welsh_court_address, court_status, court_open_date, court_name, venue_name, is_case_management_location, is_hearing_location,location_type,is_temporary_location)
values(11, '123459','Aberdeen Tribunal Hearing Centre 11',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','1','10', '2',true,'AB10, 57 HUNTLY STREET, ABERDEEN','AB11 5QA',null,null,null,null,null,null,'Open', null,'ABERDEEN TRIBUNAL HEARING CENTRE 11',null,'N','N','NBC','N');
insert into court_venue (court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id, open_for_public, court_address, postcode, phone_number, closed_date, court_location_code, dx_address, welsh_site_name, welsh_court_address, court_status, court_open_date, court_name, venue_name, is_case_management_location, is_hearing_location,location_type,is_temporary_location)
values(12, '123460','Stoke-on-Trent Combined Court',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','1','10', '2',true,'BETHESDA STREET','ST1 3BP',null,null,null,null,null,null,'Open', null,'STOKE-ON-TRENT COMBINED COURT',null,'N','N','NBC','N');

commit;
