package uk.gov.hmcts.reform.locationrefapi;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.locationrefapi.controllers.WelcomeController;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.OK;

public class WelcomeControllerTest {

    private final WelcomeController welcomeController = new WelcomeController();

    @Test
    public void shouldReturnWelcomeResponse() {

        ResponseEntity<String> responseEntity = welcomeController.welcome();
        String expectedMessage = "Welcome to location ref api";
        assertNotNull(responseEntity, "response not null");
        assertEquals(OK, responseEntity.getStatusCode(), "HttpStatus OK");
        assertThat(
            responseEntity.getBody(),
            containsString(expectedMessage)
        );
    }
}
