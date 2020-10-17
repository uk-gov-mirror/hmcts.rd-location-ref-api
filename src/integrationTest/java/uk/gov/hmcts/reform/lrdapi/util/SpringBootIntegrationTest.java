package uk.gov.hmcts.reform.lrdapi.util;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.lrdapi.LrdApiApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LrdApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SpringBootIntegrationTest {

    @LocalServerPort
    protected int port;

}
