package uk.gov.hmcts.reform.lrdapi;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;

@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
@ActiveProfiles("functional")
public class RetrieveOrgServiceDetailsFunctionalTest  extends AuthorizationFunctionalTest {
}
