package uk.gov.hmcts.reform.lrdapi;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import uk.gov.hmcts.reform.lrdapi.client.LrdApiClient;
import uk.gov.hmcts.reform.lrdapi.client.S2sClient;
import uk.gov.hmcts.reform.lrdapi.config.Oauth2;
import uk.gov.hmcts.reform.lrdapi.config.TestConfigProperties;
import uk.gov.hmcts.reform.lrdapi.idam.IdamOpenIdClient;

import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;


@ContextConfiguration(classes = {TestConfigProperties.class, Oauth2.class})
@ComponentScan("uk.gov.hmcts.reform.lrdapi")
@TestPropertySource("classpath:application-functional.yaml")
@Slf4j
@TestExecutionListeners(listeners = {
    AuthorizationFunctionalTest.class,
    DependencyInjectionTestExecutionListener.class})
public class AuthorizationFunctionalTest extends AbstractTestExecutionListener {

    @Value("${s2s-url}")
    protected String s2sUrl;

    @Value("${s2s-name}")
    protected String s2sName;

    @Value("${s2s-secret}")
    protected String s2sSecret;

    @Value("${targetInstance}")
    protected String lrdApiUrl;

    protected LrdApiClient lrdApiClient;

    protected RequestSpecification bearerToken;

    protected IdamOpenIdClient idamOpenIdClient;

    public static final String EMAIL_TEMPLATE = "freg-test-user-%s@prdfunctestuser.com";
    @Autowired
    protected TestConfigProperties configProperties;

    protected static  String  s2sToken;

    public static final String EMAIL = "EMAIL";

    public static final String CREDS = "CREDS";

    @Override
    public void beforeTestClass(TestContext testContext) {
        testContext.getApplicationContext()
            .getAutowireCapableBeanFactory()
            .autowireBean(this);

        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.defaultParser = Parser.JSON;

        log.info("Configured S2S secret: " + s2sSecret.substring(0, 2) + "************" + s2sSecret.substring(14));
        log.info("Configured S2S microservice: " + s2sName);
        log.info("Configured S2S URL: " + s2sUrl);

        SerenityRest.proxy("proxyout.reform.hmcts.net", 8080);
        RestAssured.proxy("proxyout.reform.hmcts.net", 8080);

        if (s2sToken == null) {
            s2sToken = new S2sClient(s2sUrl, s2sName, s2sSecret).signIntoS2S();
        }
        idamOpenIdClient = new IdamOpenIdClient(configProperties);
        lrdApiClient = new LrdApiClient(lrdApiUrl,s2sToken, idamOpenIdClient);

    }

    public static String generateRandomEmail() {
        return String.format(EMAIL_TEMPLATE, randomAlphanumeric(10));
    }

}
