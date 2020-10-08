
ALTER TABLE ORG_UNIT ALTER COLUMN org_unit_id
SET DEFAULT nextval('ORG_UNIT_SEQ');

ALTER TABLE ORG_BUSINESS_AREA ALTER COLUMN business_area_id
SET DEFAULT nextval('ORG_BUSINESS_AREA_SEQ');

ALTER TABLE ORG_SUB_BUSINESS_AREA ALTER COLUMN sub_business_area_id
SET DEFAULT nextval('ORG_SUB_BUSINESS_AREA_SEQ');

ALTER TABLE JURISDICTION ALTER COLUMN jurisdiction_id
SET DEFAULT nextval('JURISDICTION_SEQ');

ALTER TABLE SERVICE ALTER COLUMN service_id
SET DEFAULT nextval('SERVICE_SEQ');

ALTER TABLE SERVICE_TO_CCD_CASE_TYPE_ASSOC ALTER COLUMN id
SET DEFAULT nextval('SERVICE_TO_CCD_CASE_TYPE_ASSOC_SEQ');

