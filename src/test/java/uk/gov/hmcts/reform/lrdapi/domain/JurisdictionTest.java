package uk.gov.hmcts.reform.lrdapi.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JurisdictionTest {

    @Test
    public void testCreateJurisdiction() {

        Jurisdiction jurisdiction = new Jurisdiction(1L, "Jurisdiction");

        assertThat(jurisdiction).isNotNull();
        assertThat(jurisdiction.getDescription()).isEqualTo("Jurisdiction");
        assertThat(jurisdiction.getJurisdictionId()).isEqualTo(1L);

        Jurisdiction jurisdictionOne = new Jurisdiction();
        jurisdictionOne.setDescription("Jurisdiction");
        jurisdictionOne.setJurisdictionId(1L);
        assertThat(jurisdiction.getDescription()).isEqualTo("Jurisdiction");
        assertThat(jurisdiction.getJurisdictionId()).isEqualTo(1L);
    }
}
