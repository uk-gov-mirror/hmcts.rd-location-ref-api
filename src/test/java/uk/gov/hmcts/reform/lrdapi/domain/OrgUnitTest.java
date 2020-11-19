package uk.gov.hmcts.reform.lrdapi.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrgUnitTest {

    @Test
    public void testOrgUnit() {

        OrgUnit orgUnit = new OrgUnit(1L,"OrgUnit");
        assertThat(orgUnit).isNotNull();
        assertThat(orgUnit.getOrgUnitId()).isEqualTo(1L);
        assertThat(orgUnit.getDescription()).isEqualTo("OrgUnit");

        OrgUnit orgUnitOne = new OrgUnit();
        orgUnitOne.setOrgUnitId(1L);
        orgUnitOne.setDescription("OrgUnit");
        assertThat(orgUnitOne).isNotNull();
        assertThat(orgUnitOne.getOrgUnitId()).isEqualTo(1L);
        assertThat(orgUnitOne.getDescription()).isEqualTo("OrgUnit");
    }
}
