create table BUILDING_LOCATION(
	building_location_id varchar(16),
	epimms_id integer,
	building_location_name varchar(256),
	created_time timestamp,
	updated_time timestamp,
	building_location_status_id varchar(16),
	area varchar(16),
	region_id varchar(16),
	cluster_id varchar(16),
	court_finder_url varchar(512),
	postcode varchar(8),
	address varchar(512),
	constraint building_location_pk primary key (building_location_id)
);

create table COURT_LOCATION(
	court_location_id varchar(16),
	epimms_id integer,
	court_location_name varchar(256),
	created_time timestamp,
	updated_time timestamp,
	region_id varchar(16),
	court_location_category_id varchar(16),
	cluster_id varchar(16),
	open_for_public boolean,
	court_address varchar(512),
	postcode varchar(8),
	phone_number varchar(16),
	closed_date timestamp,
	court_location_code varchar(8),
	dx_address varchar(16),
	welsh_court_location_name varchar(256),
	welsh_court_address varchar(512),
	constraint court_id_pk primary key (court_location_id)
	constraint court_location_code_uq unique (court_location_code)
);

ALTER TABLE BUILDING_LOCATION ADD CONSTRAINT region_id_fk FOREIGN KEY (region_id)
REFERENCES REGION (region_id);

ALTER TABLE BUILDING_LOCATION ADD CONSTRAINT status_id_fk FOREIGN KEY (building_location_status_id)
REFERENCES BUILDING_LOCATION_STATUS (building_location_status_id);

ALTER TABLE BUILDING_LOCATION ADD CONSTRAINT cluster_id_fk FOREIGN KEY (cluster_id)
REFERENCES CLUSTER (cluster_id);

ALTER TABLE COURT_LOCATION ADD CONSTRAINT region_id_fk FOREIGN KEY (region_id)
REFERENCES REGION (region_id);

ALTER TABLE COURT_LOCATION ADD CONSTRAINT epimms_id_fk FOREIGN KEY (epimms_id)
REFERENCES BUILDING_LOCATION (epimms_id);

ALTER TABLE COURT_LOCATION ADD CONSTRAINT cluster_id_fk FOREIGN KEY (cluster_id)
REFERENCES CLUSTER (cluster_id);

ALTER TABLE COURT_LOCATION ADD CONSTRAINT location_category_id_fk FOREIGN KEY (location_category_id)
REFERENCES COURT_LOCATION_CATEGORY (location_category_id);

