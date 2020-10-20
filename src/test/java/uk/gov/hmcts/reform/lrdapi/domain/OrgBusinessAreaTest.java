package uk.gov.hmcts.reform.lrdapi.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrgBusinessAreaTest {

    @Test
    public void testOrgBusinessArea() {

        OrgBusinessArea orgBusinessArea = new OrgBusinessArea(1L, "BusinessArea");
        assertThat(orgBusinessArea).isNotNull();
        assertThat(orgBusinessArea.getBusinessAreaId()).isEqualTo(1L);
        assertThat(orgBusinessArea.getDescription()).isEqualTo("BusinessArea");

        OrgBusinessArea orgBusinessAreaOne = new OrgBusinessArea();
        orgBusinessAreaOne.setBusinessAreaId(1L);
        orgBusinessAreaOne.setDescription("BusinessArea");
        assertThat(orgBusinessAreaOne).isNotNull();
        assertThat(orgBusinessAreaOne.getBusinessAreaId()).isEqualTo(1L);
        assertThat(orgBusinessAreaOne.getDescription()).isEqualTo("BusinessArea");
    }
}
