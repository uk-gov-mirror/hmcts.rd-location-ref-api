package uk.gov.hmcts.reform.lrdapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.lrdapi.domain.Cluster;
import uk.gov.hmcts.reform.lrdapi.repository.ClusterRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ClusterMigrationIntegrationTest extends LrdAuthorizationEnabledIntegrationTest {

    private static final Map<String, String> EXPECTED_CLUSTERS = Map.of(
        "19", "Wales",
        "20", "Scotland",
        "21", "National",
        "23", "London",
        "24", "Northern Ireland"
    );

    @Autowired
    private ClusterRepository clusterRepository;

    @Test
    void shouldMigrateAdditionalClusterIds() {
        Map<String, String> actualClusters = clusterRepository.findAllById(EXPECTED_CLUSTERS.keySet())
            .stream()
            .collect(Collectors.toMap(Cluster::getClusterId, Cluster::getClusterName));

        assertThat(actualClusters).containsExactlyInAnyOrderEntriesOf(EXPECTED_CLUSTERS);
    }

    @Test
    void shouldNotCreateMissingClusterId22() {
        List<Cluster> clusters = clusterRepository.findAllById(List.of("22"));

        assertThat(clusters).isEmpty();
    }
}
