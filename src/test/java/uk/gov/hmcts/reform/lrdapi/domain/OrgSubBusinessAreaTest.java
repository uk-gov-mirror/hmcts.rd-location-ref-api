package uk.gov.hmcts.reform.lrdapi.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrgSubBusinessAreaTest {

    @Test
    public void testOrgSubBusinessArea() {

        OrgSubBusinessArea orgSubBusinessArea = new OrgSubBusinessArea(1L, "OrgSubBusArea");
        assertThat(orgSubBusinessArea).isNotNull();
        assertThat(orgSubBusinessArea.getSubBusinessAreaId()).isEqualTo(1L);
        assertThat(orgSubBusinessArea.getDescription()).isEqualTo("OrgSubBusArea");

        OrgSubBusinessArea orgSubBusinessAreaOne = new OrgSubBusinessArea();
        orgSubBusinessAreaOne.setSubBusinessAreaId(1L);
        orgSubBusinessAreaOne.setDescription("OrgSubBusArea");
        assertThat(orgSubBusinessAreaOne).isNotNull();
        assertThat(orgSubBusinessAreaOne.getSubBusinessAreaId()).isEqualTo(1L);
        assertThat(orgSubBusinessAreaOne.getDescription()).isEqualTo("OrgSubBusArea");
    }
}
