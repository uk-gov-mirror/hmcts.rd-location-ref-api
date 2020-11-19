package uk.gov.hmcts.reform.lrdapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.gov.hmcts.reform.idam.client.IdamApi;

@EnableJpaAuditing
@EnableJpaRepositories
@SpringBootApplication
@EnableCircuitBreaker
@EnableFeignClients(basePackages = {
    "uk.gov.hmcts.reform.lrdapi" },
    basePackageClasses = { IdamApi.class }
)
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class LrdApiApplication {

    public static void main(final String[] args) {
        SpringApplication.run(LrdApiApplication.class, args);
    }
}
